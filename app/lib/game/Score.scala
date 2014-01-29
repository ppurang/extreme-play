package lib.game

import akka.actor.Actor
import akka.event.LoggingReceive

import PlayerProtocol._
import talk._
import models.Task

sealed trait ScoreEvents

case class ScoreChangeAnswered(task: TaskAnswered, playerUUID: String, scoreChange: Int, timestamp: Long = System.currentTimeMillis()) extends ScoreEvents

case class ScoreChangeFailed(task: TaskAnswerFailed, playerUUID: String, scoreChange: Int, timestamp: Long = System.currentTimeMillis()) extends ScoreEvents

class Scorer extends Actor {
  val listenToEvents = Set(classOf[PlayerProtocol.TaskAnswered], classOf[PlayerProtocol.TaskAnswerFailed])
  listenToEvents.map {
    clss => context.system.eventStream subscribe(self, clss)
  }

  override def receive = LoggingReceive {
    case m@TaskAnswered(teamName: String, answer: Answer, task: Task) => handleTaskAnswered(teamName, answer, task, m)
    case m@TaskAnswerFailed(teamName: String, error: Error, task: Task) => handleTaskAnsweredFailed(teamName, error, task, m)
  }

  def handleTaskAnswered(teamName: String, answer: Answer, task: Task, taskAnswered: TaskAnswered) = {
    val score = verifyAnswer(answer, task)
    val eventScore = ScoreChangeAnswered(taskAnswered, teamName, score)
    context.system.eventStream publish eventScore
  }

  def handleTaskAnsweredFailed(teamName: String, error: Error, task: Task, taskAnsweredFailed: TaskAnswerFailed) = {
    val eventScore = ScoreChangeFailed(taskAnsweredFailed, teamName, Scorer.PENALTY_SCORE)
    context.system.eventStream publish eventScore
  }

  def verifyAnswer(answer: Answer, task: Task): Integer = task.scoreAnswer(answer.text)

}

object Scorer {
  val PENALTY_SCORE = -100 //todo this should be -SCORE
}