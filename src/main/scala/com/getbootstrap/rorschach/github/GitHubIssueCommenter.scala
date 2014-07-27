package com.getbootstrap.rorschach.github

import scala.util.{Try,Failure,Success}
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.RepositoryId
import com.getbootstrap.rorschach.server.Settings


class GitHubIssueCommenter extends GitHubActorWithLogging {
  // val settings = Settings(context.system)

  private def tryToCommentOn(repo: RepositoryId, issue: IssueNumber, commentMarkdown: String) = {
    val issueService = new IssueService(gitHubClient)
    Try { issueService.createComment(repo, issue.number, commentMarkdown) }
  }

  override def receive = {
    case PullRequestFeedback(prNum, requester, messages) => {
      val username = requester.getLogin
      val messagesMarkdown = messages.map{ "* " + _ }.mkString("\n")
      val commentMarkdown = s"""
        |Hi @${username}!
        |
        |Thanks for wanting to contribute to Bootstrap by sending this pull request.
        |Unfortunately, your pull request seems to have some problems:
        |${messagesMarkdown}
        |
        |You'll need to **fix these mistakes** and revise your pull request before we can proceed further.
        |Thanks!
        |
        |(*Please note that this is a fully automated comment.*)
      """.stripMargin

      tryToCommentOn(BootstrapRepoId, prNum, commentMarkdown) match {
        case Success(comment) => log.info(s"Successfully posted comment ${comment.getUrl} for ${prNum}")
        case Failure(exc) => log.error(exc, s"Error posting comment for ${prNum}")
      }
    }
  }
}
