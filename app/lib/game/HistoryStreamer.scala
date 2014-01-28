package lib.game

import akka.actor.Actor
import akka.event.LoggingReceive
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Enumerator.Pushee
import play.api.libs.iteratee.Concurrent.Channel


/*
class HistoryStreamer(val channel: Channel[HistoryEvent]) extends Actor {
  context.system.eventStream.subscribe(self, classOf[HistoryEvent])

  override def receive = LoggingReceive {
    case e : ScoreChangeAnswered | e : ScoreChangeFailed => {
      channel.push(e)
    }
  }

  override def postStop = context.system.eventStream.unsubscribe(self)

}*/
