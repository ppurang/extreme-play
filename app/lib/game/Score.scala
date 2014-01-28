package lib.game

import akka.actor.Actor
import akka.event.LoggingReceive


sealed trait ScoreEvents
case class ScoreChange(playerUUID: String, scoreChange: Int) extends ScoreEvents

class Scorer extends Actor {
   val listenToEvents = Set(classOf[PlayerProtocol.TaskAnswered], classOf[PlayerProtocol.TaskAnswerFailed])
   listenToEvents.map{ clss => context.system.eventStream subscribe (self,clss) }
  
   override def receive = LoggingReceive {
     case _ => 
   }
}