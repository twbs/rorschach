package com.getbootstrap.rorschach.github

import org.eclipse.egit.github.core.User

case class PullRequestFeedback(
  prNum: IssueNumber,
  requester: User,
  messages: Seq[String]
)
