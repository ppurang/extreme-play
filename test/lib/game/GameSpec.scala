package lib.game

import org.scalatest.{FunSpec, BeforeAndAfterAll}
import akka.testkit.{TestActorRef, TestKit}
import akka.actor.ActorSystem
import lib.game.GameProtocol.PlayerRegistered
import org.scalatest.matchers.ShouldMatchers

class GameSpec extends TestKit(ActorSystem("test-system"))
  with FunSpec with BeforeAndAfterAll with ShouldMatchers {

  describe("The Game actor") {
    it("should create new actor for a registered player and remember it") {
      val game = TestActorRef[Game]
      game ! PlayerRegistered("team-foo", "http:localhost:9000")
      game.underlyingActor.playersByName.contains("team-foo") should equal(true)
    }
  }

  override protected def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }


}
