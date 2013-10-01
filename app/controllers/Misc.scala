package controllers

import play.api.mvc._
import Results._
import play.api.Play._

object Misc {

  //this is an alternative to play.mvc.Results.TODO
  //it provides some possibility for more information to help debugging
  //in addition to that NotImplemented i.e. 501 status raised by TODO is meant for METHODS and not resources
  def ???(context: String) = ServiceUnavailable(context)

  val serverBaseUrl = current.configuration.getString("http.base.url").get //fail fast

}
