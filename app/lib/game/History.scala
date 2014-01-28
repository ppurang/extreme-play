package lib.game

import akka.actor.Actor
import lib.game.History.{HistoryResponse, GetHistory, HistoryEvent}
import lib.game.GameProtocol._
import akka.event.LoggingReceive

object History {
  case class HistoryEvent(playerId: String)
  case class GetHistory(playerId: String)
  case class HistoryResponse(playerId: String, history: Option[Vector[HistoryEvent]])
}
class History extends Actor {

  Set(
    classOf[HistoryEvent],
    classOf[PlayerRegistered],
    classOf[PlayerUnregistered]) foreach { c =>
    context.system.eventStream.subscribe(self, c)
  }

  def receive = updated(Map.empty[String, Vector[HistoryEvent]])

  private def updated(state: Map[String, Vector[HistoryEvent]]): Receive = LoggingReceive {
    case PlayerRegistered(name, url, uuid) ⇒ {
      val newState = state updated(uuid, Vector())
      context become updated(newState)
    }
    case PlayerUnregistered(name, uuid) ⇒ {
      val newState = state - uuid
      context become updated(newState)
    }
    case GetHistory(playerId) =>
      sender ! HistoryResponse(playerId, state.get(playerId))
    case e: HistoryEvent =>
      val newState = if (state.keySet(e.playerId)) {
        state map { case (k, v) => if (k == e.playerId) (k, v :+ e) else (k, v)}
      }
      else {
        state updated(e.playerId, Vector(e))
      }
      context become updated(newState)
  }

}
