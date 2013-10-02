package lib.game

import akka.actor.{Props, Actor}
import akka.event.LoggingReceive

object Player {
  def props(teamName: String, url: String): Props = Props(classOf[Player], teamName, url)
}
class Player(name: String, url: String) extends Actor {
  override def receive = LoggingReceive {
    case _ =>
  }
}
