package com.getbootstrap.rorschach

import java.nio.charset.Charset
import scala.util.Try

package object util {
  private val utf8 = Charset.forName("UTF-8")

  implicit class Utf8String(str: String) {
    def utf8Bytes: Array[Byte] = str.getBytes(utf8)
  }

  implicit class Utf8ByteArray(bytes: Array[Byte]) {
    def utf8String: Try[String] = Try { new String(bytes, utf8) }
  }

  implicit class CaseSensitiveString(str: String) {
    import java.util.Locale

    def asciiLowerCased: String = str.toLowerCase(Locale.ENGLISH)
  }

  implicit class SplittableString(str: String) {
    /**
     * Python's str.partition()
     */
    def splitOnce(separator: String): (String, String) = {
      str.lastIndexOf(separator) match {
        case -1 => (str, "")
        case sepStart => snipOut(sepStart, separator.length)
      }
    }

    /**
     * Python's str.rpartition()
     */
    def splitOnceFromRight(separator: String): (String, String) = {
      str.lastIndexOf(separator) match {
        case -1 => ("", str)
        case sepStart => snipOut(sepStart, separator.length)
      }
    }

    private def snipOut(start: Int, length: Int): (String, String) = {
      val prefix = str.substring(0, start)
      val suffix = str.substring(start + length)
      (prefix, suffix)
    }
  }

  implicit class FilepathString(filepath: String) {
    def onlyFilename: String = filepath.splitOnceFromRight("/")._2
  }

  implicit class FilenameString(filename: String) {
    def withoutExtension: String = {
      if (filename.length >= 1 && filename.charAt(0) == '.') {
        filename
      }
      else {
        filename.splitOnce(".")._1
      }
    }
  }
}
