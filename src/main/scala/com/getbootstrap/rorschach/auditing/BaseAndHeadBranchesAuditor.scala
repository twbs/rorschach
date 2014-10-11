package com.getbootstrap.rorschach.auditing

object BaseAndHeadBranchesAuditor {
  def audit(baseBranch: String, headBranch: String): Seq[String] = {
    Seq(
      auditThatNotAgainstGhPages(baseBranch),
      auditThatNotMergingGhPagesIntoMaster(baseBranch = baseBranch, headBranch = headBranch)
    ).flatten
  }

  def auditThatNotAgainstGhPages(baseBranch: String): Option[String] = {
    baseBranch match {
      case "gh-pages" => Some("[Normal pull requests should never be against the `gh-pages` branch.](https://github.com/twbs/rorschach/blob/master/docs/against-gh-pages.md)")
      case _ => None
    }
  }

  def auditThatNotMergingGhPagesIntoMaster(baseBranch: String, headBranch: String): Option[String] = {
    (headBranch, baseBranch) match {
      case ("gh-pages", "master") => Some("[Normally, the `gh-pages` branch should never be merged into `master` branch.](https://github.com/twbs/rorschach/blob/master/docs/gh-pages-into-master.md)")
      case _ => None
    }
  }
}
