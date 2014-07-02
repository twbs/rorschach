package com.getbootstrap.rorschach.github

sealed trait CommitFileStatus {
  val StatusString: String
}
object Added extends CommitFileStatus {
  override val StatusString = "added"
}
object Modified extends CommitFileStatus {
  override val StatusString = "modified"
}
object Deleted extends CommitFileStatus {
  override val StatusString = "deleted"
}
object Unknown extends CommitFileStatus {
  override val StatusString = "UNKNOWN"
}

object CommitFileStatus {
  def apply(status: String): CommitFileStatus = {
    status match {
      case Added.StatusString => Added
      case Modified.StatusString => Modified
      case Deleted.StatusString => Deleted
      case _ => Unknown
    }
  }
}
