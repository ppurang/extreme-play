package models

import concurrent.stm._
import scala.util.Try
import java.util.UUID
import lib.game.Game
import lib.game.GameProtocol
import play.api.libs.json._
import play.GlobalSettings
import play.api.libs.concurrent.Akka

case class Player(uid: String, name: String, url: String, state: Player.State, playerAuth: String, serverId: String) {
  import Player._
  
  def isAuthCorrect(playerAuth: String) = playerAuth == this.playerAuth

  def isServerCorrect(serverId: String) = serverId == this.serverId
    
  def toggleState: Player = this.copy(state = if (state == Paused) Running else Paused)
}

object Player {
  def apply(name: String, url: String): Player = Player(uuid, name, url, Running, uuid, uuid)
  
  sealed trait State
  case object Paused extends State
  case object Running extends State
  
  implicit val writePlayer = Writes[Player] { v =>
    Json.obj("uid" -> v.uid, "name" -> v.name, "url" -> v.url, "state" -> v.state.toString)
  }
  val writePlayerSensitive = Writes[Player] { v =>
    writePlayer.writes(v).asInstanceOf[JsObject] ++ 
      Json.obj("playerAuth" -> v.playerAuth, "serverId" -> v.serverId)
  }
  
  private val ref = Ref(Seq[Player]()).single

  def register(newPlayer: NewPlayer): Try[Player] = {
    val NewPlayer(name, url) = newPlayer
    val player = Player(name, url)

    val createTry = Try {
      ref.transform { players =>
        require(players forall (_.name != name), "A player with this name already exsits")
        require(players forall (_.url != url), "URL is already used by an other player")
        players :+ player
      }
      player
    }

    createTry.foreach { player =>
      import play.api.Play.current
      val evt = GameProtocol.PlayerRegistered(player.name, player.url, player.uid)
      Game.ref ! evt
      Akka.system.eventStream.publish(evt)
    }

    createTry
  }

  def all: Seq[Player] = ref.get
  
  def find(uid: String, secret: String): Option[Player] =
    ref.get.find(p => p.uid == uid && p.isAuthCorrect(secret))

  def unregister(uid: String, secret: String): Unit =
    find(uid, secret).map { player =>
      ref.transform(_.filterNot(_ == player))
    }
  
  def toggleState(uid: String, secret: String): Option[Player] =
    ref.transformAndExtract { players =>
      find(uid, secret).map { oldPlayer =>
        val toggledPlayer = oldPlayer.toggleState
        (players.filterNot(_ == oldPlayer) :+ toggledPlayer, Some(toggledPlayer))
      }.getOrElse((players, None))
    }

  private[Player] def uuid() = UUID.randomUUID.toString
}
