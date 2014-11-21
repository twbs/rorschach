package com.getbootstrap.rorschach.server

import akka.actor.ActorRef
import spray.routing._
import spray.http._


class RorschachActor(protected val pullRequestEventHandler: ActorRef) extends ActorWithLogging with HttpService {
  import GitHubPullRequestWebHooksDirectives.authenticatedPullRequestEvent

  private val settings = Settings(context.system)
  override def actorRefFactory = context
  override def receive = runRoute(theOnlyRoute)

  val theOnlyRoute =
    pathPrefix("rorschach") { pathEndOrSingleSlash {
      get {
        complete(StatusCodes.OK, "Hi! Rorschach is online.")
      } ~
      post {
        headerValueByName("X-Github-Event") { githubEvent =>
          githubEvent match {
            case "ping" => {
              log.info("Successfully received GitHub webhook ping.")
              complete(StatusCodes.OK)
            }
            case "pull_request" => {
              authenticatedPullRequestEvent(settings.WebHookSecretKey.toArray) { event =>
                event.getAction match {
                  case "opened" | "synchronize" => {
                    val pr = event.getPullRequest
                    if (pr.getState == "open") {
                      pullRequestEventHandler ! pr
                      complete(StatusCodes.OK)
                    }
                    else {
                      complete(StatusCodes.OK, s"Ignoring event about closed pull request #${pr.getId}")
                    }
                  }
                  case _ => complete(StatusCodes.OK, "Ignoring irrelevant action")
                }
              }
            }
            case _ => complete(StatusCodes.BadRequest, "Unexpected event type")
          }
        }
      }
    }}
}
