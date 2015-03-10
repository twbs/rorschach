package com.getbootstrap.rorschach.github

import org.eclipse.egit.github.core.client.GitHubClient
import com.getbootstrap.rorschach.server.{Settings, ActorWithLogging}

abstract class GitHubActorWithLogging extends ActorWithLogging {
  protected val settings = Settings(context.system)
  protected val gitHubClient = new GitHubClient()
  gitHubClient.setUserAgent(settings.UserAgent)
  gitHubClient.setCredentials(settings.BotUsername, settings.BotPassword)
}
