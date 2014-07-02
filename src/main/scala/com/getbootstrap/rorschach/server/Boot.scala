package com.getbootstrap.rorschach.server

import scala.concurrent.duration._
import scala.util.Try
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.routing.SmallestMailboxPool
import akka.util.Timeout
import com.getbootstrap.rorschach.github.GitHubIssueCommenter


object Boot extends App {
  val arguments = args.toSeq
  val maybePort = arguments match {
    case Seq(portStr: String) => {
      Try{ portStr.toInt }.toOption
    }
    case _ => None
  }
  maybePort match {
    case Some(port) => run(port)
    case _ => {
      System.err.println("USAGE: rorschach <port-number>")
      System.exit(1)
    }
  }

  def run(port: Int) {
    implicit val system = ActorSystem("on-spray-can")
    // import actorSystem.dispatcher

    val commenter = system.actorOf(SmallestMailboxPool(3).props(Props(classOf[GitHubIssueCommenter])), "gh-pr-commenter")
    val prAuditorPool = system.actorOf(SmallestMailboxPool(5).props(Props(classOf[PullRequestEventHandler], commenter)), "pr-auditor-pool")
    val webService = system.actorOf(Props(classOf[RorschachActor], prAuditorPool), "rorschach-service")

    implicit val timeout = Timeout(15.seconds)
    IO(Http) ? Http.Bind(webService, interface = "0.0.0.0", port = port)
  }
}
