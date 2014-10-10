package com.getbootstrap.rorschach.github

object PullRequestStatus {
  def apply(value: String): PullRequestStatus = {
    value match {
      case Open.Value => Open
      case Closed.Value => Closed
      case _ => throw new IllegalArgumentException(s"Invalid pull request status string: ${value}")
    }
  }
}
sealed trait PullRequestStatus {
  val Value: String
}
object Open extends PullRequestStatus {
  override val Value = "open"
}
object Closed extends PullRequestStatus {
  override val Value = "closed"
}
