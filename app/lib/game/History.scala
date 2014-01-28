package lib.game

import akka.actor.Actor
import lib.game.History.{HistoryResponse, GetHistory, HistoryEvent}

object History {
  case class HistoryEvent(playerId: String)
  case class GetHistory(playerId: String)
  case class HistoryResponse(playerId: String, history: Vector[HistoryEvent])
}
class History extends Actor {

  context.system.eventStream.subscribe(self, classOf[HistoryEvent])

  def receive = updated(Map.empty[String, Vector[HistoryEvent]])

  private def updated(state: Map[String, Vector[HistoryEvent]]): Receive = {
    case GetHistory(playerId) =>
      sender ! HistoryResponse(playerId, state.getOrElse(playerId, Vector.empty))
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
