package com.getbootstrap.rorschach.server

import scala.util.{Success, Failure, Try}
import spray.routing.{Directive1, ValidationRejection}
import spray.routing.directives.{BasicDirectives, RouteDirectives}
import org.eclipse.egit.github.core.event.PullRequestPayload
import org.eclipse.egit.github.core.client.GsonUtils

trait GitHubPullRequestWebHooksDirectives {
  import RouteDirectives.reject
  import BasicDirectives.provide
  import HubSignatureDirectives.stringEntityMatchingHubSignature

  def authenticatedPullRequestEvent(secretKey: Array[Byte]): Directive1[PullRequestPayload] = stringEntityMatchingHubSignature(secretKey).flatMap{ entityJsonString =>
    Try { GsonUtils.fromJson(entityJsonString, classOf[PullRequestPayload]) } match {
      case Failure(exc) => reject(ValidationRejection("JSON was either malformed or did not match expected schema!"))
      case Success(payload) => provide(payload)
    }
  }
}

object GitHubPullRequestWebHooksDirectives extends GitHubPullRequestWebHooksDirectives
