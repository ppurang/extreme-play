package models

import concurrent.stm._
import scala.util.Try

case class Player(name: String, url: String)

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
