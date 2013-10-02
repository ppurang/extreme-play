package talk

import scala.language.postfixOps

import scala.concurrent._

import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import scala.util.{Try, Success, Failure}

case class User(name: String, url: String) {

  def ping: Future[Boolean] = WS.url(url).withRequestTimeout(User.timeout).get.map { response =>
    response.status match {
      case 200 => true
      case _ => false
    }
  }

  def ask(question: Question): Future[Answer] =
    WS.url(url).withRequestTimeout(User.timeout).post(question.toJson).map { response =>
      import response._

      status match {
        case 200  => Answer.fromJson(json)
        case code => throw Error(code, statusText, body, question.uuid)
      }
    }
}

object User {
  val timeout = 5000
}

case class Question(text: String, uuid: String = uuid) {
  def toJson: JsValue = Json.obj("query" -> text, "uuid" -> uuid)
}
case class Answer(text: String, uuid: String, questionUUID: String)

object Answer {
  def fromJson(json: JsValue): Answer = Answer(json \ "text" toString, uuid, json \ "question" toString)
}

case class Error(statusCode: Int, statusText: String, message: String, questionUUID: String) extends Exception(message)
