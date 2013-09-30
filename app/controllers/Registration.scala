package controllers

import play.api.mvc._
import play.api.libs.json._
import models._
import NewPlayer._
object Registration extends Controller {
  def register = Action(parse.json) { request =>
    request.body.validate[NewPlayer].map{
      case NewPlayer(name, url) => Ok("Hello " + name + ", you're "+url)
    }.recoverTotal{
      e => BadRequest("Faulty payload: "+ JsError.toFlatJson(e))
    }
  }

}
