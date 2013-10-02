package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class NewPlayer(name: String, url: String)

object NewPlayer {
  implicit val newPlayerFormat : Format[NewPlayer] = Json.format[NewPlayer]
}

case class Task(query: String, verify: String => Boolean, score: Int = 0)