package com.getbootstrap.rorschach.server

import com.getbootstrap.rorschach.auditing.{BaseAndHeadBranchesAuditor, ModifiedFilesAuditor}

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import com.jcabi.github.User
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.jcabi.github.Pull.{Smart=>PullRequest}
import com.getbootstrap.rorschach.github._
import com.getbootstrap.rorschach.github.implicits._

class PullRequestEventHandler(commenter: ActorRef) extends GitHubActorWithLogging {
  private def modifiedFilesFor(repoId: RepoId, base: CommitSha, head: CommitSha) = {
    Try { gitHubClient.repos.get(repoId).commits.compare(base.sha, head.sha) }.map{ comparison =>
      val affectedFiles = comparison.smart.files.asScala
      affectedFiles.filter{ _.status == Modified }.map{ _.getFilename }.toSet[String]
    }
  }

  def isTrusted(user: User): Boolean = {
    settings.TrustedOrganizations.exists{ org => Try{ orgService.isPublicMember(org, user.getLogin) }.toOption.getOrElse(false) }
  }

  // FIXME: caller needs to pass SmartPull
  override def receive = {
    case pr: PullRequest => {
      val bsBase = pr.getBase
      val prHead = pr.getHead
      val destinationRepo = bsBase.getRepo.repositoryId
      if (settings.repoIds contains destinationRepo) {
        if (isTrusted(pr.getUser)) {
          log.info(s"Skipping audit because user ${pr.getUser.getLogin} is member of trusted org.")
        }
        else {
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
            commenter ! PullRequestFeedback(destinationRepo, pr.number, pr., allMessages)
          }
          else {
            log.info(s"Repo ${destinationRepo} ${pr.number} successfully passed all audits.")
          }
        }
      }
      else {
        log.error(s"Received event from GitHub about irrelevant repository: ${destinationRepo}")
      }
    }
  }
}
