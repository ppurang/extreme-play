package lib.game

import akka.actor._
import akka.event.LoggingReceive
import play.api.libs.concurrent.Akka

object GameProtocol {
  case class PlayerRegistered(name: String, url: String)
}

object Game {
  val ref: ActorRef = Akka.system.actorOf(Props[Game])
}

class Game extends Actor with ActorLogging {
  import GameProtocol._
  private[game] var playersByName = Map.empty[String, ActorPath]
  override def receive = LoggingReceive {
    case PlayerRegistered(name, url) =>
      playersByName += (name -> context.actorOf(Player.props(name, url)).path)

  }
}
