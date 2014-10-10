package com.getbootstrap.rorschach.server

import scala.collection.JavaConverters._
import com.typesafe.config.Config
import akka.actor.ActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import akka.util.ByteString
import org.eclipse.egit.github.core.RepositoryId
import com.getbootstrap.rorschach.util.Utf8String

class SettingsImpl(config: Config) extends Extension {
  val repoIds: Set[RepositoryId] = config.getStringList("rorschach.github-repos-to-watch").asScala.toSet.map{ (repoFullName: String) => RepositoryId.createFromId(repoFullName) }
  val BotUsername: String = config.getString("rorschach.username")
  val BotPassword: String = config.getString("rorschach.password")
  val WebHookSecretKey: ByteString = ByteString(config.getString("rorschach.web-hook-secret-key").utf8Bytes)
  val DefaultPort: Int = config.getInt("rorschach.default-port")
  val CloseBadPullRequests: Boolean = config.getBoolean("rorschach.close-bad-pull-requests")
}
object Settings extends ExtensionId[SettingsImpl] with ExtensionIdProvider {
  override def lookup() = Settings
  override def createExtension(system: ExtendedActorSystem) = new SettingsImpl(system.settings.config)
  override def get(system: ActorSystem): SettingsImpl = super.get(system)
}
