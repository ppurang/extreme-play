package controllers

import play.api.mvc._
import play.api.libs.json._
import models._
import NewPlayer._

object Registration extends Controller {

  def register = Action(parse.json) { request =>
    request.body.validate[NewPlayer].map {
      case newPlayer: NewPlayer =>
        val player = models.Player.register(newPlayer)
        import player._
        Ok(s"Hello $name, you're $url")
    }.recoverTotal {
      e => BadRequest("Faulty payload: " + JsError.toFlatJson(e))
    }
  }

}
