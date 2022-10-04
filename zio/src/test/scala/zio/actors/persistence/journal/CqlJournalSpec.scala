package zio.actors.persistence.journal

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import zio.Unsafe
import zio.actors.persistence.PersistenceId
import org.scalatest.BeforeAndAfterAll
import zio.actors.persistence.journal.CassandraJournal

class CassandraJournalSpec extends AsyncWordSpec with Matchers {
  import CassandraJournalSpec._

  "CassandraJournal" should {
    "should persist and get events" in {
      val db            = CqlClient()
      val journal       = new CassandraJournal[Incremented](db, 300)
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

object CassandraJournalSpec {
  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
  case class Incremented(amount: Int)
}
