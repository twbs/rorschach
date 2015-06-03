package com.getbootstrap.rorschach.github

import com.jcabi.github.{Issue,Pull}
import com.jcabi.github.Issue.{Smart=>SmartIssue}
import com.jcabi.github.Pull.{Smart=>SmartPullRequest}

package object util {
  /*implicit class RichRepository(repo: Repository) {
    def repositoryId: RepositoryId = new RepositoryId(repo.getOwner.getLogin, repo.getName)
  }*/
  implicit class RichPullRequestMarker(marker: PullRequestMarker) {
    def commitSha: CommitSha = new CommitSha(marker.getSha)
  }
  implicit class RichCommitFile(file: CommitFile) {
    def status: CommitFileStatus = CommitFileStatus(file.getStatus)
  }
  /*implicit class RichPullRequest(pr: Pull) {
    def prNumber: PullRequestNumber = PullRequestNumber(pr.number).get
    def smart: SmartPullRequest = new SmartPullRequest(pr)
  }*/

}
