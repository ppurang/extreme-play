package lib.game

import akka.actor.Actor


sealed trait ScoreEvents;
case class ScoreChange(playerUUID: String, scoreChange: Int) extends ScoreEvents;

class Scorer extends Actor {
  
}