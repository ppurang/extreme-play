package controllers

import play.api.Play.current
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._


object VisibilityTesting extends Controller {
  val defaultTimeout = 2000
  val timeout = current.configuration.getInt("http.test.timeout").getOrElse(defaultTimeout)


  def test(url: String) = Action.async {
    WS.url(url).withRequestTimeout(timeout).get().map { response =>
      if (response.status == 200) Connected else NotConnected
    }.recover {
      case _ => NotConnected
    }.map(_.answer(url))
  }

  class PlayerStatus(status: Status, description: String) {
    def answer(url: String) = status(s"http payload: Tested: $url $description")
  }
  case object Connected extends PlayerStatus(Ok, "connected")
  case object NotConnected extends PlayerStatus(NotFound, "not connected")
}
