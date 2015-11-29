package com.getbootstrap.rorschach.auditing

import com.getbootstrap.rorschach.util._

object AddedFilesAuditor {
  private val message =
    """[The fact that this pull request adds a new file named something like `new-file.txt` indicates that you were using Bootstrap to test out Git or GitHub, rather than proposing a legitimate change.](https://github.com/twbs/rorschach/blob/master/docs/newfile.md)
      |If that's accurate, please **DON'T** do that again!
      |Instead, [use *your own personal repositories*](https://guides.github.com/activities/hello-world/) to experiment with [how to use Git or GitHub](https://help.github.com/articles/good-resources-for-learning-git-and-github/).
      |Using the repos of public projects (such as Bootstrap) for such experiments wastes the time of those projects' maintainers
      |and is thus considered rude.""".stripMargin.replaceAllLiterally("\n", " ")

  def audit(addedFilepaths: Set[String]): Seq[String] = {
    val filenames = addedFilepaths.map{ filepath => {
      filepath.onlyFilename.withoutExtension.replaceAllLiterally("-", "").replaceAllLiterally(" ", "").asciiLowerCased
    }}
    if (filenames.contains("newfile")) {
      Seq(message)
    }
    else {
      Nil
    }
  }
}
