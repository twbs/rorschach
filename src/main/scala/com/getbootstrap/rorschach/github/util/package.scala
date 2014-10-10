package com.getbootstrap.rorschach.github

import org.eclipse.egit.github.core._

package object util {
  implicit class RichRepository(repo: Repository) {
    def repositoryId: RepositoryId = new RepositoryId(repo.getOwner.getLogin, repo.getName)
  }
  implicit class RichPullRequestMarker(marker: PullRequestMarker) {
    def commitSha: CommitSha = new CommitSha(marker.getSha)
  }
  implicit class RichCommitFile(file: CommitFile) {
    def status: CommitFileStatus = CommitFileStatus(file.getStatus)
  }
  implicit class RichPullRequest(pr: PullRequest) {
    def number: PullRequestNumber = PullRequestNumber(pr.getNumber).get

    def status_= (value: PullRequestStatus) {
      pr.setState(value.Value)
    }
    def status = PullRequestStatus(pr.getState)
  }
}
