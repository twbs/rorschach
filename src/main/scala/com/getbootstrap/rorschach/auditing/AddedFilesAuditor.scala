package com.getbootstrap.rorschach.auditing

import org.eclipse.egit.github.core.CommitFile
import com.getbootstrap.rorschach.util._
import com.getbootstrap.rorschach.github.util._

object AddedFilesAuditor {
  private val postamble =
    """If that's accurate, please **DON'T** do that again!
      |Instead, [use *your own personal repositories*](https://guides.github.com/activities/hello-world/) to experiment with [how to use Git or GitHub](https://help.github.com/articles/good-resources-for-learning-git-and-github/).
      |Using the repos of public projects (such as Bootstrap) for such experiments wastes the time of those projects' maintainers
      |and is thus considered rude.""".stripMargin.replaceAllLiterally("\n", " ")

  private val filenameMessage =  "[The fact that this pull request adds a new file named something like `new-file.txt` indicates that you were using Bootstrap to test out Git or GitHub, rather than proposing a legitimate change.](https://github.com/twbs/rorschach/blob/master/docs/newfile.md) " + postamble

  private val undefinedMessage = """[The fact that this pull request adds a new file whose content solely consists of the word "undefined" indicates that you were using Bootstrap to test out Git or GitHub, rather than proposing a legitimate change.](https://github.com/twbs/rorschach/blob/master/docs/undefined-content.md) """ + postamble

  private val sillyUndefPatch = "@@ -0,0 +1 @@\n+undefined\n\\ No newline at end of file"

  def audit(addedFiles: Seq[CommitFile]): Seq[String] = {
    auditFilenames(addedFiles.filenames) ++ auditFileContent(addedFiles)
  }

  private def auditFilenames(filepaths: Set[String]): Seq[String] = {
    val filenames = filepaths.map{ filepath => {
      filepath.onlyFilename.withoutExtension.replaceAllLiterally("-", "").replaceAllLiterally(" ", "").asciiLowerCased
    }}
    if (filenames.contains("newfile")) {
      Seq(filenameMessage)
    }
    else {
      Nil
    }
  }

  private def auditFileContent(addedFiles: Seq[CommitFile]): Seq[String] = {
    if (addedFiles.iterator.exists{ _.getPatch == sillyUndefPatch }) {
      Seq(undefinedMessage)
    }
    else {
      Nil
    }
  }
}
