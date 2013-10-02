package controllers

import play.api.mvc._
import play.api.libs.json._
import models._
import NewPlayer._
import play.api.Play._

object Registration extends Controller {
  val unregisterable = current.configuration.getBoolean("feature.player.unregister-able").getOrElse(false)
  
  implicit val writePlayerPublic = new Writes[models.Player] {
    def writes(v: models.Player): JsValue = {
      Json.obj("uid" -> v.uid.toString(), "name" -> v.name, "url" -> v.url)
    }
  }
  
  def findAll = Action(parse.empty) { implicit req => 
    Ok(Json.toJson(models.Player.all))
  }

  def register = Action(parse.json) { implicit request =>
    request.body.validate[NewPlayer].map {
      case newPlayer: NewPlayer =>
        val attemp = models.Player.register(newPlayer)
        attemp.map { player =>
          Created(Json.toJson(player)(models.Player.sensitivPlayer)).
          withHeaders(
            "Location" ->
              routes.History.player(uid.toString).absoluteURL().toString
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
