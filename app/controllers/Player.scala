package controllers

import play.api.mvc._

object Player extends Controller {
  
  def register = Action {
    Created("1").withHeaders("Location" -> "http://localhost:9000/player/1")
  }
  
}