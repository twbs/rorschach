package com.getbootstrap.rorschach.server

import com.getbootstrap.rorschach.auditing.{BaseAndHeadBranchesAuditor, ModifiedFilesAuditor}

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.service.CommitService
import com.getbootstrap.rorschach.github._
import com.getbootstrap.rorschach.github.util._

class PullRequestEventHandler(commenter: ActorRef) extends GitHubActorWithLogging {


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
      val destinationRepo = bsBase.getRepo.repositoryId
      if (settings.repoIds contains destinationRepo) {
        log.info(s"Auditing ${destinationRepo} ${pr.number} ...")
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
          commenter ! PullRequestFeedback(destinationRepo, pr.number, pr.getUser, allMessages)
        }
      }
      else {
        log.error(s"Received event from GitHub about irrelevant repository: ${destinationRepo}")
      }
    }
  }
}
