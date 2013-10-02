package lib.game

import akka.actor.{Props, Actor}
import akka.event.LoggingReceive
import lib.game.Player.NewTaskRequired
import lib.game.PlayerProtocol.GameStarted

object PlayerProtocol {
  case object GameStarted
}

object Player {
  def props(teamName: String, url: String): Props = Props(classOf[Player], teamName, url)

  private case object NewTaskRequired
}
class Player(name: String, url: String) extends Actor {
  override def receive = LoggingReceive {
    case GameStarted     =>
    case NewTaskRequired =>
  }
}
