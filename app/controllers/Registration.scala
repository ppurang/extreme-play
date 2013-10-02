package controllers

import play.api.mvc._
import play.api.libs.json.Json.toJson
import models._
import NewPlayer._
import play.api.Play._

object Registration extends Controller {
  val unregisterable = current.configuration.getBoolean("feature.player.unregister-able").getOrElse(false)
  
  def findAll = Action(parse.empty) { implicit req => 
    Ok(toJson(models.Player.all))
  }

  def register = Action(parse.json) { implicit request =>
    request.body.validate[NewPlayer].map {
      case newPlayer: NewPlayer =>
        val attemp = models.Player.register(newPlayer)
        attemp.map { player =>
          Created(toJson(player)(models.Player.sensitivPlayer)).
          withHeaders(
            "Location" ->
              routes.History.player(player.uid.toString).absoluteURL().toString
          )
        }.getOrElse {
          Conflict("Name or URL already in use.")
        }

    }.recoverTotal {
      e => BadRequest("Faulty payload: " + JsError.toFlatJson(e))
    }
  }

  def unregister(uid: String) = Action(parse.anyContent) { request =>
    if (unregisterable) {
      request.headers.get("player-auth-key").map(models.Player.unregister(uid, _))
      NoContent
    } else NotFound("Disabled in configuration")
  }
}
