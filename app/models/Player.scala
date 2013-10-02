package models

import concurrent.stm._
import scala.util.Try
import java.util.UUID

case class Player(name: String, url: String) {
  private val playerAuth = UUID.randomUUID
  private val serverId = UUID.randomUUID
  
  def isAuthCorrect(playerAuth: UUID) =
    playerAuth == this.playerAuth
  
  def isServerCorrect(serverId: UUID) =
    serverId == this.serverId
  
  val uid = UUID.randomUUID
}

object Player {
  private val ref = Ref(Seq[Player]()).single

  def register(newPlayer: NewPlayer): Try[Player] = {
    val NewPlayer(name, url) = newPlayer
    val player = Player(name, url)

    Try {
      ref.transform { players =>
        assert(players forall (_.name != name))
        assert(players forall (_.url != url))
        players :+ player
      }
      player
    }
  }

  def all: Seq[Player] = ref.get
}
