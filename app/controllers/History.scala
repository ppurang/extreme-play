package controllers

import play.api.mvc._
import lib.game.Game
import lib.game.History.{HistoryEvent, HistoryResponse, GetHistory}
import play.api.libs.json._
import akka.util.Timeout

object History extends Controller {

  def player(uid: String) = Action.async {
    implicit req =>
      import akka.pattern.ask
      import concurrent.duration._
      import scala.concurrent.ExecutionContext.Implicits.global
      implicit val timeout = Timeout(200.millis)
      implicit val historyEventFormat = Json.format[HistoryEvent]
      implicit val historyResponseFormat = Json.format[HistoryResponse]
      (Game.history ? GetHistory(uid)).mapTo[HistoryResponse].map(x => Ok(Json.toJson(x)))
  }

}
