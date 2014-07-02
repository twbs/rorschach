package com.getbootstrap.rorschach.server

import com.getbootstrap.rorschach.auditing.{BaseAndHeadBranchesAuditor, ModifiedFilesAuditor}

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import com.getbootstrap.rorschach.github._
import org.eclipse.egit.github.core.service.CommitService
import org.eclipse.egit.github.core._

class PullRequestEventHandler(commenter: ActorRef) extends GitHubActorWithLogging {
  implicit class RichRepository(repo: Repository) {
    def repositoryId: RepositoryId = new RepositoryId(repo.getOwner.getLogin, repo.getName)
  }
  implicit class RichPullRequestMarker(marker: PullRequestMarker) {
    def commitSha: CommitSha = new CommitSha(marker.getSha)
  }
  implicit class RichCommitFile(file: CommitFile) {
    def status: CommitFileStatus = CommitFileStatus(file.getStatus)
  }

  private def modifiedFilesFor(repoId: RepositoryId, base: CommitSha, head: CommitSha) = {
    val commitService = new CommitService(gitHubClient)
    Try { commitService.compare(repoId, base.sha, head.sha) }.map { comparison =>
      val affectedFiles = comparison.getFiles.asScala
      affectedFiles.filter{ _.status == Modified }.map{ _.getFilename }.toSet[String]
    }
  }

  override def receive = {
    case pr: PullRequest => {
      val bsBase = pr.getBase
      val prHead = pr.getHead
      bsBase.getRepo.repositoryId match {
        case BootstrapRepoId => {
          val base = bsBase.commitSha
          val head = prHead.commitSha
          val foreignRepoId = prHead.getRepo.repositoryId

          val fileMessages = modifiedFilesFor(foreignRepoId, base, head) match {
            case Failure(exc) => {
              log.error(exc, s"Could not get modified files for commits ${base}...${head} for ${foreignRepoId}")
              Nil
            }
            case Success(modifiedFiles) => {
              ModifiedFilesAuditor.audit(modifiedFiles)
            }
          }
          val branchMessages = BaseAndHeadBranchesAuditor.audit(baseBranch = bsBase.getRef, headBranch = prHead.getRef)

          val allMessages = fileMessages ++ branchMessages
          if (allMessages.nonEmpty) {
            commenter ! PullRequestFeedback(pr.getNumber, pr.getUser, allMessages)
          }
        }
        case otherRepo => log.error(s"Received event from GitHub about irrelevant repository: ${otherRepo}")
      }
    }
  }
}
