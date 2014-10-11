package com.getbootstrap.rorschach.github

import scala.util.{Try,Failure,Success}
import org.eclipse.egit.github.core.service.{PullRequestService, IssueService}
import org.eclipse.egit.github.core.RepositoryId
import com.getbootstrap.rorschach.github.util.RichPullRequest
import com.getbootstrap.rorschach.server.Settings


class GitHubPullRequestCommenter extends GitHubActorWithLogging {
  // val settings = Settings(context.system)

  private def tryToCommentOn(repo: RepositoryId, prNum: PullRequestNumber, commentMarkdown: String) = {
    val issueService = new IssueService(gitHubClient)
    Try { issueService.createComment(repo, prNum.number, commentMarkdown) }
  }

  private def tryToClose(repo: RepositoryId, prNum: PullRequestNumber): Try[None.type] = {
    val prService = new PullRequestService(gitHubClient)
    val prTry = Try { prService.getPullRequest(repo, prNum.number) } match {
      case fetchFail@Failure(exc) => {
        log.error(exc, s"Error fetching pull request ${prNum} in order to close it")
        fetchFail
      }
      case Success(pr) => {
        pr.status = Closed
        Try { prService.editPullRequest(repo, pr) }
      }
    }
    prTry.map{ x => None }
  }

  override def receive = {
    case PullRequestFeedback(repo, prNum, requester, messages) => {
      val username = requester.getLogin
      val messagesMarkdown = messages.map{ "* " + _ }.mkString("\n")
      val commentMarkdown = s"""
        |Hi @${username}!
        |
        |Thanks for wanting to contribute to Bootstrap by sending this pull request!
        |Unfortunately, your pull request seems to have some problems:
        |${messagesMarkdown}
        |
        |You'll need to **fix these mistakes** and revise your pull request before we can proceed further.
        |Thanks!
        |
        |(*Please note that this is a [fully automated](https://github.com/twbs/rorschach) comment.*)
      """.stripMargin

      tryToCommentOn(repo, prNum, commentMarkdown) match {
        case Success(comment) => log.info(s"Successfully posted comment ${comment.getUrl} for ${prNum}")
        case Failure(exc) => log.error(exc, s"Error posting comment for ${prNum}")
      }

      if (settings.CloseBadPullRequests) {
        tryToClose(repo, prNum) match {
          case Success(_) => log.info(s"Successfully closed ${prNum} due to failed audit(s)")
          case Failure(exc) => log.error(exc, s"Error closing ${prNum}")
        }
      }
    }
  }
}
