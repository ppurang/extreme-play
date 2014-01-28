package lib.game

import akka.actor.Actor
import akka.event.LoggingReceive

sealed trait ScoreEvents
case class ScoreChange(playerUUID: String, scoreChange: Int, timestamp : Long = System.currentTimeMillis()) extends ScoreEvents

class Scorer extends Actor {
   import PlayerProtocol._
   import talk._
   import models.Task

   val listenToEvents = Set(classOf[PlayerProtocol.TaskAnswered], classOf[PlayerProtocol.TaskAnswerFailed])
   listenToEvents.map{ clss => context.system.eventStream subscribe (self,clss) }
  
   override def receive = LoggingReceive {
    case TaskAnswered(teamName: String, answer: Answer, task: Task) => handleTaskAnswered(teamName, answer, task)
    case TaskAnswerFailed(teamName: String, error: Error, task: Task) => handleTaskAnsweredFailed(teamName, error, task)
   }

   def handleTaskAnswered(teamName: String, answer: Answer, task: Task) = {
   	val score = verifyAnswer(answer, task)
   	val eventScore = ScoreChange(teamName, score)
   	context.system.eventStream publish eventScore
   }

   def handleTaskAnsweredFailed(teamName: String, error: Error, task: Task) = {
   	val eventScore = ScoreChange(teamName, Scorer.PENALTY_SCORE)
   	context.system.eventStream publish eventScore
   }

   def verifyAnswer(answer: Answer, task: Task) : Integer = {
   	val f = task.verify(answer.text)
   	if (f) task.score
   	else -task.score
   }
}

object Scorer {
	val PENALTY_SCORE = -100
}