package controllers

import play.api.mvc._
import java.util.UUID
import play.Play
import play.api.Play.current

object Player extends Controller {

  val server = current.configuration.getString("http.base.url")
  
  def register(uid: String) = Action {
      try {
        UUID.fromString(uid)
        Created(s"$uid").withHeaders("Location" -> s"$server/player/$uid")
      }
      catch {
        case e:IllegalArgumentException => BadRequest(s"$uid isn't a uuid")
      }
  }
  
}