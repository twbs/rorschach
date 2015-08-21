package com.getbootstrap.rorschach.auditing

object ModifiedFilesAuditor {
  private implicit class PathString(filepath: String) {
    def isDist: Boolean = filepath.startsWith("dist/")
    def isNonMinifiedCss: Boolean = filepath.endsWith(".css") && !filepath.endsWith(".min.css")
    def isSourceLess: Boolean = filepath.startsWith("less/") && filepath.endsWith(".less")
    def isSourceScss: Boolean = filepath.startsWith("scss/") && filepath.endsWith(".scss")
    def isNonMinifiedJs: Boolean = filepath.endsWith(".js") && !filepath.endsWith(".min.js")
    def isDistCss: Boolean = filepath.isDist && filepath.isNonMinifiedCss
    def isDistJs: Boolean = filepath.isDist && filepath.isNonMinifiedJs
    def isSourceJs: Boolean = filepath.startsWith("js/") && filepath.isNonMinifiedJs
  }

  def audit(filepaths: Set[String]): Seq[String] = {
    Seq(auditCname(filepaths), auditCss(filepaths), auditJs(filepaths)).flatten
  }

  /**
   * If dist/bootstrap.css etc. is modified, then less/<*>.less must also have been modified.
   */
  private def auditCss(filepaths: Set[String]): Option[String] = {
    val cssModified = filepaths.exists{ _.isDistCss }
    val lessModified = filepaths.exists{ _.isSourceLess }
    val scssModified = filepaths.exists{ _.isSourceScss }
    if (cssModified && !lessModified && !scssModified) {
      Some("[Changes must be made to the original Sass or Less source code file(s), not just the compiled CSS file(s).](https://github.com/twbs/rorschach/blob/master/docs/css.md)")
    }
    else {
      None
    }
  }

  /**
   * If dist/js/bootstrap.js etc. is modified, then js/<*>.js must also have been modified.
   */
  private def auditJs(filepaths: Set[String]): Option[String] = {
    val distJsModified = filepaths.exists{ _.isDistJs }
    val sourceJsModified = filepaths.exists{ _.isSourceJs }
    if (distJsModified && !sourceJsModified) {
      Some("[Changes must be made to the original JS source code file(s), not just the generated concatenated JS file(s).](https://github.com/twbs/rorschach/blob/master/docs/js.md)")
    }
    else {
      None
    }
  }

  private def auditCname(filepaths: Set[String]): Option[String] = {
    if (filepaths.contains("CNAME")) {
      Some("[The `CNAME` file should never be modified.](https://github.com/twbs/rorschach/blob/master/docs/cname.md)")
    }
    else {
      None
    }
  }
}
