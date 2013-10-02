package controllers

import play.api.Play.current
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._


object VisibilityTesting extends Controller {
  val connected = "connected"
  val notConnected = "not connected"
  val defaultTimeout = 2000

  val timeout = current.configuration.getInt("http.test.timeout").getOrElse(defaultTimeout)


  def test(url: String) = Action.async {
    WS.url(url).withRequestTimeout(timeout).get().map { response =>
      answer(url, if (response.status == 200) connected else notConnected)
    }.recover {
      case _ => answer(url, notConnected)
    }
  }

  private def answer(url: String, connected: String) = Ok(s"http payload: Tested: $url $connected")
}
