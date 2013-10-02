package models

import concurrent.stm._

case class Player(name: String, url: String)

object Player {
  private val ref = Ref(Seq[Player]()).single

  def register(newPlayer: NewPlayer): Player = {
    val NewPlayer(newName, url) = newPlayer
    val player = Player(newName, url)
    
    ref.transform{
      _.filter(_.name != newName) :+ player
    }
    
    player
  }

  def all: Seq[Player] = ref.get
}
