package com.getbootstrap.rorschach.github

import com.jcabi.github.{CommitsComparison,Issue,Pull}
import com.jcabi.github.CommitsComparison.{Smart=>SmartCommitsComparison}
import com.jcabi.github.Issue.{Smart=>SmartIssue}
import com.jcabi.github.Pull.{Smart=>SmartPull}

package object implicits {
  implicit class RichIssue(issue: Issue) {
    def smart: SmartIssue = new SmartIssue(issue)
  }
  implicit class RichCommitsComparison(comparison: CommitsComparison) {
    def smart: SmartCommitsComparison = new SmartCommitsComparison(comparison)
  }
  implicit class RichPull(pull: Pull) {
    def smart: SmartPull = new SmartPull(pull)
  }
}
