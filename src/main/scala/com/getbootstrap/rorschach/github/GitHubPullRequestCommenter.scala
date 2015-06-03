package com.getbootstrap.rorschach.github

import scala.util.{Try,Failure,Success}
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.getbootstrap.rorschach.github.implicits._
import com.getbootstrap.rorschach.server.Settings


class GitHubPullRequestCommenter extends GitHubActorWithLogging {
  // val settings = Settings(context.system)

  private def tryToCommentOn(repo: RepoId, prNum: PullRequestNumber, commentMarkdown: String) = {
    Try { gitHubClient.repos.get(repo).issues.get(prNum.number).comments.post(commentMarkdown) }
  }

  private def tryToClose(repo: RepoId, prNum: PullRequestNumber): Try[Unit] = {
    val closure = Try { gitHubClient.repos.get(repo).issues.get(prNum.number).smart.close() }
    closure match {
      case Failure(exc) => {
        log.error(exc, s"Error closing pull request ${prNum}")
      }
      case _ => {}
    }
    closure
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
        |Once you've fixed these problems, you can either ask the maintainers to re-open this pull request, or you can create a new pull request.
        |Thanks!
        |
        |(*Please note that this is a [fully automated](https://github.com/twbs/rorschach) comment.*)
      """.stripMargin

      tryToCommentOn(repo, prNum, commentMarkdown) match {
        case Success(comment) => log.info(s"Successfully posted comment ${comment.smart.url} for ${prNum}")
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
