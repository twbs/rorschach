package com.getbootstrap.rorschach.github

import org.eclipse.egit.github.core.{RepositoryId,User}

case class PullRequestFeedback(
  repo: RepositoryId,
  prNum: PullRequestNumber,
  requester: User,
  messages: Seq[String]
)
