package lib.game

import akka.actor.Actor
import lib.game.History.{HistoryResponse, GetHistory}
import lib.game.GameProtocol._
import akka.event.LoggingReceive

object History {
  case class GetHistory(playerId: String)
  case class HistoryResponse(playerId: String, history: Option[Vector[ScoreChangeAnswered]])
}
class History extends Actor {
  Set(
    classOf[PlayerRegistered],
    classOf[PlayerUnregistered],
    classOf[ScoreChangeAnswered]
  ) foreach { c =>
    context.system.eventStream.subscribe(self, c)
  }

  def receive = {
    updated(Map.empty[String, Vector[ScoreChangeAnswered]])
  }

  private def updated(state: Map[String, Vector[ScoreChangeAnswered]]): Receive = {
    LoggingReceive {
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
      case e @ ScoreChangeAnswered(_, _, _, _) ⇒ {
        val newState = if (state.keySet(e.playerUUID)) {
          state map { case (k, v) => if (k == e.playerUUID) (k, v :+ e) else (k, v)}
        }
        else {
          state updated(e.playerUUID, Vector(e))
        }
        context become updated(newState)
      }
    }
  }

}
