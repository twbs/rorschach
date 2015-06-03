package com.getbootstrap.rorschach.github

import com.getbootstrap.rorschach.server.{Settings, ActorWithLogging}

abstract class GitHubActorWithLogging extends ActorWithLogging {
  protected val settings = Settings(context.system)
  protected val gitHubClient = settings.github()
}
