package controllers

import play.api.Play.current
import play.api.mvc._


object VisibilityTesting extends Controller {

  val server = current.configuration.getString("http.base.url").get //fail fast

  def test(url: String) = Action {
    ServiceUnavailable("I can't really test right now. " + url)
  }

}