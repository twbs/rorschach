package com.getbootstrap.rorschach.server

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.service.{CommitService, OrganizationService}
import com.getbootstrap.rorschach.auditing._
import com.getbootstrap.rorschach.github._
import com.getbootstrap.rorschach.github.util._

class PullRequestEventHandler(commenter: ActorRef) extends GitHubActorWithLogging {
  private def affectedFilesFor(repoId: RepositoryId, base: CommitSha, head: CommitSha) = {
    val commitService = new CommitService(gitHubClient)
    Try { commitService.compare(repoId, base.sha, head.sha) }.map { comparison =>
      comparison.getFiles.asScala
    }
  }

  def isTrusted(user: User): Boolean = {
    val orgService = new OrganizationService(gitHubClient)
    settings.TrustedOrganizations.exists{ org => Try{ orgService.isPublicMember(org, user.getLogin) }.toOption.getOrElse(false) }
  }

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

          val affectedFiles = affectedFilesFor(foreignRepoId, base, head) match {
            case Failure(exc) => {
              log.error(exc, s"Could not get affected files for commits ${base}...${head} for ${foreignRepoId}")
              Nil
            }
            case Success(files) => files
          }
          val modifiedFiles = affectedFiles.filter{ _.status == Modified }.filenames
          val addedFiles = affectedFiles.filter{ _.status == Added }.filenames

          val titleMessages = TitleAuditor.audit(pr.getTitle)
          val branchMessages = BaseAndHeadBranchesAuditor.audit(baseBranch = bsBase.getRef, headBranch = prHead.getRef)
          val fileMessages = ModifiedFilesAuditor.audit(modifiedFiles) ++ AddedFilesAuditor.audit(addedFiles)
          val allMessages = titleMessages ++ branchMessages ++ fileMessages
          if (allMessages.nonEmpty) {
            commenter ! PullRequestFeedback(destinationRepo, pr.number, pr.getUser, allMessages)
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
