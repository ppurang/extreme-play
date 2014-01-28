package talk

import scala.language.postfixOps

import scala.concurrent._

import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import scala.util.{Random, Try, Success, Failure}

import scala.concurrent.duration._

case class User(name: String, url: String) {

  def ping: Future[Boolean] = WS.url(url).withRequestTimeout(User.timeout).get.map { response =>
    response.status match {
      case 200 => true
      case _ => false
    }
  }

  def ask(question: Question): Future[Answer] = {
    val ms = System.currentTimeMillis
    val request = WS.url(url).withRequestTimeout(User.timeout)
    val rsp =
      if (Random.nextBoolean())
        request.withQueryString(question.toQueryParameter: _*).get()
      else
        request.post(question.toJson)
    rsp.collect {
        case response =>
          import response._

      status match {
        case 200  => Answer.fromJson(json).copy(duration = (System.currentTimeMillis - ms))
        case code => throw Error(code, statusText, body, question.uuid)
      }
    }
  }
}

object User {
  val timeout = 5000
}

case class Question(text: String, uuid: String = uuid) {
  def toJson: JsValue = Json.obj("query" -> text, "uuid" -> uuid)
  def toQueryParameter = Seq(("q", text), ("uuid", uuid))
}
case class Answer(text: String, uuid: String, questionUUID: String, duration: Long = -1)

object Answer {
  def fromJson(json: JsValue): Answer = Answer(json \ "text" toString, uuid, json \ "question" toString)
}

case class Error(statusCode: Int, statusText: String, message: String, questionUUID: String) extends Exception(message)
