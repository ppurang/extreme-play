package controllers

import play.api.mvc._
import lib.game.Game
import lib.game.History.{HistoryResponse, GetHistory}
import play.api.libs.json._
import akka.util.Timeout

object History extends Controller {

  def player(uid: String) = Action.async {
    implicit req =>
      import akka.pattern.ask
      import concurrent.duration._
      import scala.concurrent.ExecutionContext.Implicits.global
      import talk.Answer
      import models.Task
      import lib.game.PlayerProtocol.TaskAnswered
      import lib.game.ScoreChangeAnswered

      implicit val timeout = Timeout(200.millis)
      implicit val answerFormat = Json.format[Answer]
      implicit val taskFormat = Json.format[models.Task]
      implicit val answeredEventFormat = Json.format[TaskAnswered]
      implicit val scoreEventFormat = Json.format[ScoreChangeAnswered]
      implicit val historyResponseFormat = Json.format[HistoryResponse]
      (Game.history ? GetHistory(uid)).mapTo[HistoryResponse].map { x =>
        x.history.fold(NotFound(""))(events => Ok(Json.toJson(events)))
      }
  }

}
