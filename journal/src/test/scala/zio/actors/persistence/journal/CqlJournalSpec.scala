package zio.actors.persistence.journal

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import zio.Unsafe
import zio.actors.persistence.PersistenceId

class CqlJournalSpec extends AsyncWordSpec with Matchers {
  import CqlJournalSpec._

  "CassandraJournal" should {
    "should persist and get events" in {
      val db            = CqlClient(CqlConfig.default)
      val journal       = new CassandraJournal[Incremented](db, 300, true)
      val persistenceId = PersistenceId("test")

      val runtime = zio.Runtime.default
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe
          .runToFuture(
            for {
              _      <- journal.persistEvent(persistenceId, Incremented(100))
              events <- journal.getEvents(persistenceId)
            } yield events
          )
          .future
          .map(_ contains Incremented(100) should be(true))
      }
    }
  }
}

object CqlJournalSpec {
  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
  case class Incremented(amount: Int)
}
