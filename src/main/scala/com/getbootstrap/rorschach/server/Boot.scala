package com.getbootstrap.rorschach.server

import scala.util.{Success,Failure}
import scala.concurrent.duration._
import scala.util.Try
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.routing.SmallestMailboxPool
import akka.util.Timeout
import com.getbootstrap.rorschach.github.GitHubPullRequestCommenter


object Boot extends App {
  val arguments = args.toSeq
  val argsPort = arguments match {
    case Seq(portStr: String) => {
      Try{ portStr.toInt } match {
        case Failure(_) => {
          System.err.println("USAGE: lmvtfy <port-number>")
          System.exit(1)
          None // dead code
        }
        case Success(portNum) => Some(portNum)
      }
    }
    case Seq() => None
  }

  run(argsPort)

  def run(port: Option[Int]) {
    implicit val system = ActorSystem("on-spray-can")
    // import actorSystem.dispatcher

    val commenter = system.actorOf(SmallestMailboxPool(3).props(Props(classOf[GitHubPullRequestCommenter])), "gh-pr-commenter")
    val prAuditorPool = system.actorOf(SmallestMailboxPool(5).props(Props(classOf[PullRequestEventHandler], commenter)), "pr-auditor-pool")
    val webService = system.actorOf(Props(classOf[RorschachActor], prAuditorPool), "rorschach-service")

    implicit val timeout = Timeout(15.seconds)
    IO(Http) ? Http.Bind(webService, interface = "0.0.0.0", port = port.getOrElse(settings.DefaultPort))
  }
}
