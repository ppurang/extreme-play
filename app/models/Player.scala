package models

import concurrent.stm._
import scala.util.Try
import java.util.UUID
import lib.game.Game
import lib.game.GameProtocol
import play.api.libs.json.Json

case class Player(name: String, url: String, 
    uid: String = UUID.randomUUID.toString, 
    playerAuth:String = UUID.randomUUID.toString, 
    serverId: String = UUID.randomUUID.toString) {
  
//  val uid = UUID.randomUUID.toString()
//  val playerAuth = UUID.randomUUID.toString()
//  val serverId = UUID.randomUUID.toString()

  def isAuthCorrect(playerAuth: String) =
    playerAuth == this.playerAuth

  def isServerCorrect(serverId: String) =
    serverId == this.serverId

}

object Player {
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
      Game.ref ! GameProtocol.PlayerRegistered(player.name, player.url)
    }

    createTry
  }

  def all: Seq[Player] = ref.get

  val sensitivPlayer = Json.format[Player]

  def unregister(uid: String, secret: String) {
    ref.get.find(p => p.uid == uid && p.isAuthCorrect(secret)).map { player =>
      ref.transform(_.filterNot(_ == player))
    }
  }
}
