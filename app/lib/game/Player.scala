package lib.game

import akka.actor.{Props, Actor}
import akka.event.LoggingReceive
import lib.game.PlayerProtocol.{TaskAnswerFailed, TaskAnswered, GameStarted}
import lib.game.Player.NewTaskRequired
import controllers.logic.infiniteTaskRepo
import talk.{Error, Answer, Question, User}

object PlayerProtocol {
  case object GameStarted
  case class TaskAnswered(teamName: String, answer: Answer)
  case class TaskAnswerFailed(teamName: String, error: Error)
}

object Player {
  def props(
      teamName: String,
      url: String,
      taskIntervalGen: TaskIntervalGenerator = defaultTaskIntervalGen): Props =
    Props(classOf[Player], teamName, url, taskIntervalGen)

  private case object NewTaskRequired
}

class Player(
    name: String,
    url: String,
    taskIntervalGen: TaskIntervalGenerator) extends Actor {
  val webservice = User(name, url)
  implicit val ec = scala.concurrent.ExecutionContext.global
  override def receive = LoggingReceive {
    case GameStarted =>
      context.system.scheduler.scheduleOnce(taskIntervalGen(), self, NewTaskRequired)
    case NewTaskRequired =>
      if (infiniteTaskRepo.select.hasNext) {
        val task = infiniteTaskRepo.select.next()
        val responseF = webservice ask Question(task.query)
        responseF onSuccess {
          case Right(answer) =>
            context.system.eventStream publish TaskAnswered(name, answer)
            self ! NewTaskRequired
          case Left(error) =>
            context.system.eventStream publish TaskAnswerFailed(name, error)
            self ! NewTaskRequired
        }
      }
  }
}
