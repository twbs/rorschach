package com.getbootstrap.rorschach.auditing

import com.getbootstrap.rorschach.util._

object TitleAuditor {
  private val message =
    """[The title of this pull request strongly suggests that you were using Bootstrap to test out Git or GitHub, rather than proposing a legitimate change.](https://github.com/twbs/rorschach/blob/master/docs/title.md)
      |If that's accurate, please **DON'T** do that again!
      |Instead, [use *your own personal repositories*](https://guides.github.com/activities/hello-world/) to experiment with [how to use Git or GitHub](https://help.github.com/articles/good-resources-for-learning-git-and-github/).
      |Using the repos of public projects (such as Bootstrap) for such experiments wastes the time of those projects' maintainers
      |and is thus considered rude.""".stripMargin.replaceAllLiterally("\n", " ")

  def audit(title: String): Seq[String] = {
    if (
      title.startsWith("Merge pull request #1 from ")
      || title.startsWith("Create ")
      || title.trim.asciiLowerCased == "master"
    ) {
      Seq(message)
    }
    else {
      Nil
    }
  }
}
