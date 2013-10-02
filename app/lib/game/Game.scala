package lib.game

import akka.actor._

import akka.event.LoggingReceive
import play.api.libs.concurrent.Akka
import play.api.Play.current

object GameProtocol {
  case class PlayerRegistered(name: String, url: String)
  case object GetRegisteredPlayers
}

object Game {
  val ref: ActorRef = Akka.system.actorOf(Props[Game])
}

class Game extends Actor with ActorLogging {
  import GameProtocol._
  private[game] var playersByName = Map.empty[String, ActorPath]
  override def receive = LoggingReceive {
    case PlayerRegistered(name, url) =>
      if (!playersByName.contains(name)){
      	val actorPlayer = context.actorOf(Player.props(name, url))
      	playersByName += (name -> actorPlayer.path)
      	actorPlayer ! PlayerProtocol.GameStarted 
      } else {
      	//player already registrated, what should we do???
      }

    case GetRegisteredPlayers =>
    	  sender ! playersByName.keySet 

  }
}
