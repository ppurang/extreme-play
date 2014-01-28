package lib.game

import akka.actor._

import akka.event.LoggingReceive
import play.api.libs.concurrent.Akka
import play.api.Play.current

object GameProtocol {
  case class PlayerRegistered(name: String, url: String)
  case object GetRegisteredPlayers
  case class PlayerUnregistered(name: String)
}

object Game {
  val ref: ActorRef = Akka.system.actorOf(Props[Game])
  val history: ActorRef = Akka.syste.actorOf(Props[History])
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
      	//player already registreted, what should we do???
      }

    case PlayerUnregistered(name) =>
    	if (!playersByName.contains(name)){
	      	//player is not registreted, what should we do???
      } else {
      		val actorPath = playersByName(name)
      		playersByName -= name
      		context.actorSelection(actorPath) ! PlayerProtocol.KillYourself
      }

    case GetRegisteredPlayers =>
    	  sender ! playersByName.keySet 

  }
}
