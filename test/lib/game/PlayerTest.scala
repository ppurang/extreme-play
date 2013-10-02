package lib.game

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.scalatest.matchers.MustMatchers
import akka.actor.ActorSystem
import scala.concurrent.duration._
import lib.game.PlayerProtocol.GameStarted

class PlayerTest extends TestKit(ActorSystem("player-test-system"))
    with FunSpec with MustMatchers with BeforeAndAfterAll {

  private val testInterval: TaskIntervalGenerator = () => 50.millis

  describe("The Player actor") {
    it("should send itself a NewTaskRequired message upon GameStarted") {
      val player = system.actorOf(Player.props("foo", "http://localhost", testInterval))
      player ! GameStarted
    }
  }

  override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }

}
