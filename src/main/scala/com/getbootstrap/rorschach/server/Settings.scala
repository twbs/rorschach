package com.getbootstrap.rorschach.server

import scala.collection.JavaConverters._
import com.typesafe.config.Config
import akka.actor.ActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import akka.util.ByteString
import com.jcabi.github.Github
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.getbootstrap.rorschach.github.Credentials
import com.getbootstrap.rorschach.http.{UserAgent=>UA}
import com.getbootstrap.rorschach.util.Utf8String

class SettingsImpl(config: Config) extends Extension {
  val repoIds: Set[RepoId] = config.getStringList("rorschach.github-repos-to-watch").asScala.toSet[String].map{ new RepoId(_) }
  val BotUsername: String = config.getString("rorschach.username")
  private val botPassword: String = config.getString("rorschach.password")
  private val botCredentials: Credentials = Credentials(username = BotUsername, password = botPassword)
  private val githubRateLimitThreshold: Int = config.getInt("rorschach.github-rate-limit-threshold")
  def github(): Github = botCredentials.github(githubRateLimitThreshold)(UserAgent)
  val WebHookSecretKey: ByteString = ByteString(config.getString("rorschach.web-hook-secret-key").utf8Bytes)
  val UserAgent: UA = UA(config.getString("spray.can.client.user-agent-header"))
  val DefaultPort: Int = config.getInt("rorschach.default-port")
  val SquelchInvalidHttpLogging: Boolean = config.getBoolean("rorschach.squelch-invalid-http-logging")
  val CloseBadPullRequests: Boolean = config.getBoolean("rorschach.close-bad-pull-requests")
  val TrustedOrganizations: Set[String] = config.getStringList("rorschach.trusted-orgs").asScala.toSet
}
object Settings extends ExtensionId[SettingsImpl] with ExtensionIdProvider {
  override def lookup() = Settings
  override def createExtension(system: ExtendedActorSystem) = new SettingsImpl(system.settings.config)
  override def get(system: ActorSystem): SettingsImpl = super.get(system)
}
