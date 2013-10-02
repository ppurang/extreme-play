package controllers

import play.api.mvc._
import play.api.libs.json._
import models._
import NewPlayer._

object Registration extends Controller {

  def register = Action(parse.json) { implicit request =>
    request.body.validate[NewPlayer].map {
      case newPlayer: NewPlayer =>
        val attemp = models.Player.register(newPlayer)
        attemp.map { player =>
          import player._
          Created(s"Hello $name, you're $url").
          withHeaders(
            "location" ->
              routes.History.player(uid.toString).absoluteURL().toString
          )
        }.getOrElse {
          Conflict("Name or URL already in use.")
        }

    }.recoverTotal {
      e => BadRequest("Faulty payload: " + JsError.toFlatJson(e))
    }
  }

  def unregister(uid: String) = Action(parse.text) { request =>
    models.Player.unregister(uid, request.body)
    NoContent
  }
}
