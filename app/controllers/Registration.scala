package controllers

import play.api.mvc._
import play.api.libs.json.Json.toJson
import play.api.libs.json.JsError.toFlatJson
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
          Created(toJson(player)(models.Player.writePlayerSensitive)).
          withHeaders(
            "Location" ->
              routes.History.player(player.uid.toString).absoluteURL().toString
          )
        }.getOrElse {
          Conflict("Name or URL already in use.")
        }

    }.recoverTotal {
      e => BadRequest("Faulty payload: " + toFlatJson(e))
    }
  }

  def unregister(uid: String) = FeaturedAction("feature.player.unregister-able") { request =>
    request.headers.get("player-auth-key").map(models.Player.unregister(uid, _))
    NoContent
  }
  
  def togglePause(uid: String) = FeaturedAction("feature.player.pause-able") { request => 
    request.headers.get("player-auth-key").map(
        models.Player.toggleState(uid, _).map { toggledPlayer => 
          toggledPlayer.state match {
            case models.Player.Paused => Ok(s"player ${toggledPlayer.name} is now pausing")
            case models.Player.Running => Ok(s"player ${toggledPlayer.name} is now running")
          }  
        }.getOrElse(Ok) //TODO player not found or no matching key
    ).getOrElse(BadRequest) //TODO no auth-key
  }
}
