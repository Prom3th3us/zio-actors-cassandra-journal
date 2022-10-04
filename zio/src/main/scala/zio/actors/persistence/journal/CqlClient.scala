package zio.actors.persistence.journal

import io.getquill.CassandraAsyncContext
import io.getquill.NamingStrategy
import io.getquill.SnakeCase

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class CqlClient(
    db: CassandraAsyncContext[SnakeCase.type]
)(implicit ec: ExecutionContext) {
  import CqlClient._
  import db._

  def insertEvent(m: Messages): Future[Unit] =
    db.run(quote { (m: Messages) =>
      query[Messages]
        .insertValue(m)
    }(lift(m)))

  def readEvents(persistenceId: String, shardId: Long): Future[List[Messages]] =
    db.run(quote { (persistenceId: String, shardId: Long) =>
      query[Messages]
        .filter(_.persistence_id == persistenceId)
        .filter(_.partition_nr == shardId)
        .sortBy(m => (m.sequence_nr, m.timestamp))
        .allowFiltering
    }(lift(persistenceId), lift(shardId)))

  def maxBy[A](
      persistenceId: String,
      shardId: Long,
      by: Messages => A
  )(implicit ord: Ordering[A]): Future[Option[A]] =
    readEvents(persistenceId, shardId)
      .map(_.map(m => by(m)).maxOption)

}

object CqlClient {
  // representation for table `messages`
  case class Messages(
      persistence_id: String,
      partition_nr: Long,
      sequence_nr: Long,
      timestamp: UUID,
      event: Array[Byte]
  )

  def apply(
      cqlConfig: CqlConfig
  )(implicit ec: ExecutionContext): CqlClient = {
    val dbClientConfig = CqlConfig.toClientConfig(cqlConfig)
    val context        = new CassandraAsyncContext(SnakeCase, dbClientConfig)
    new CqlClient(context)
  }
}
