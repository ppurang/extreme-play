package lib.game

import akka.actor.{ActorPath, ActorLogging, Actor}
import akka.event.LoggingReceive

object GameProtocol {
  case class PlayerRegistered(name: String, url: String)
}

object Game {

}

class Game extends Actor with ActorLogging {
  import GameProtocol._
  private[game] var playersByName = Map.empty[String, ActorPath]
  override def receive = LoggingReceive {
    case PlayerRegistered(name, url) =>
      playersByName += (name -> context.actorOf(Player.props(name, url)).path)
  }
}
