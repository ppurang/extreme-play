package lib.game

import org.scalatest.{FunSpec, BeforeAndAfterAll}
import akka.testkit.{TestActorRef, TestKit}
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}
import akka.pattern.ask
import akka.util.Timeout
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

    it("should returned the list of registered users") {
      val game = TestActorRef[Game]
      game ! PlayerRegistered("Martin Odersky", "http:localhost:9000")
      game ! PlayerRegistered("Viktor Klang", "http:localhost:9000")
      implicit val timeout = akka.util.Timeout(30.seconds)
      val future = game ? GameProtocol.GetRegisteredPlayers
      val Success(registeredPlayers) = future.value.get
      registeredPlayers should equal(Set("Martin Odersky","Viktor Klang"))
    }

  }

  override protected def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }


}
