package controllers

import play.api.mvc._
import play.api.libs.json._
import models._
import NewPlayer._

object Registration extends Controller {

  def register = Action(parse.json) { request =>
    request.body.validate[NewPlayer].map {
      case newPlayer: NewPlayer =>
        val attemp = models.Player.register(newPlayer)
        attemp.map { player =>
          import player._
          Ok(s"Hello $name, you're $url")
        }.getOrElse {
          Conflict("Name or URL already in use.")
        }

    }.recoverTotal {
      e => BadRequest("Faulty payload: " + JsError.toFlatJson(e))
    }
  }

}
