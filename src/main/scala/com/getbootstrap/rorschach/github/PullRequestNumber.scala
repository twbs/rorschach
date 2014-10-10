package com.getbootstrap.rorschach.github

object PullRequestNumber {
  def apply(number: Int): Option[PullRequestNumber] = {
    if (number > 0) {
      Some(new PullRequestNumber(number))
    }
    else {
      None
    }
  }
}
class PullRequestNumber private(val number: Int) extends AnyVal {
  override def toString = s"PullRequestNumber(${number})"
}
