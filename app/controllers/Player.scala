package controllers

import play.api.mvc._
import java.util.UUID

object Player extends Controller {
  
  def register(uid: String) = Action {
      try {
        UUID.fromString(uid)
        Created(s"$uid").withHeaders("Location" -> s"http://localhost:9000/player/$uid")
      }
      catch {
        case e:IllegalArgumentException => BadRequest(s"$uid isn't a uuid")
      }
  }
  
}