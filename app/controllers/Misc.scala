package controllers

import play.api.mvc._
import Results._
import play.api.Play._

object Misc {

  def ???(context: String) = ServiceUnavailable(context)

  val serverBaseUrl = current.configuration.getString("http.base.url").get //fail fast

}
