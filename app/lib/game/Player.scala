package lib.game

import akka.actor.{Props, Actor, PoisonPill}
import akka.event.LoggingReceive
import lib.game.PlayerProtocol._
import lib.game.Player.NewTaskRequired
import controllers.logic.infiniteTaskRepo
import talk.{Error, Answer, Question, User}
import scala.util.{Success, Failure}

object PlayerProtocol {
  case object GameStarted
  case class TaskAnswered(teamName: String, answer: Answer)
  case class TaskAnswerFailed(teamName: String, error: Error)
  case object KillYourself
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
  var deathImminent = false
  val webservice = User(name, url)
  implicit val ec = scala.concurrent.ExecutionContext.global
  override def receive = LoggingReceive {
    case GameStarted =>
      context.system.scheduler.scheduleOnce(taskIntervalGen(), self, NewTaskRequired)
    case NewTaskRequired =>
      if (infiniteTaskRepo.select.hasNext) {
        val task = infiniteTaskRepo.select.next()
        val responseF = webservice ask Question(task.query)
        responseF onComplete {
          case Success(answer) =>
            context.system.eventStream publish TaskAnswered(name, answer)
            scheduleNewTask
          case Failure(error: Error) =>
            context.system.eventStream publish TaskAnswerFailed(name, error)
            scheduleNewTask
          case _ =>
        }
      }
      case KillYourself => 
        this.deathImminent = true
  }

  private[Player] def scheduleNewTask() {
    if (!this.deathImminent){
        self ! NewTaskRequired
      } else {
        self ! PoisonPill
      } 
  }
}
