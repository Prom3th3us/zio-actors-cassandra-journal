package example.actor

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import zio.{ Unsafe, _ }
import zio.actors.{ ActorSystem, Supervisor }

import java.io.File

class GuildEventSourcedSpec extends AsyncWordSpec with Matchers {

  "GuildEventSourced" should {
    "restart and recover state" in {
      import example.actor.GuildEventSourced._
      val configPath = "example/src/main/resources/application.conf"

      val io = for {
        actorSystem    <- ActorSystem("GuildSystem", Some(new File(configPath)))
        persistenceId1 <- Random.nextUUID.map(_.toString).map(id => s"guild1-$id")
        persistenceId2 <- Random.nextUUID.map(_.toString).map(id => s"guild2-$id")
        user1          <- Random.nextUUID.map(_.toString)
        user2          <- Random.nextUUID.map(_.toString)
        user3          <- Random.nextUUID.map(_.toString)
        user4          <- Random.nextUUID.map(_.toString)
        // Scenario 1
        guild1 <- actorSystem
          .make(persistenceId1, Supervisor.none, GuildState.empty, handler(persistenceId1))
        _ <- guild1 ? Join(user1)
        _ <- guild1 ? Join(user2)
        _ <- guild1 ? Get
        _ <- guild1.stop
        // Scenario 2
        guild2 <- actorSystem
          .make(persistenceId2, Supervisor.none, GuildState.empty, handler(persistenceId2))
        _        <- guild2 ? Join(user1)
        _        <- guild2 ? Join(user2)
        members2 <- guild2 ? Get
        _        <- guild2.stop
        // Scenario 3
        guild1B <- actorSystem
          .make(persistenceId1, Supervisor.none, GuildState.empty, handler(persistenceId1))
        _         <- guild1B ? Join(user3)
        _         <- guild1B ? Join(user4)
        members1b <- guild1B ? Get
        _         <- guild1B.stop
      } yield members1b.members == (members2.members ++ Set(user3, user4))

      val runtime = zio.Runtime.default
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe
          .runToFuture(io)
          .future
          .map(_ should be(true))
      }
    }
  }
}
