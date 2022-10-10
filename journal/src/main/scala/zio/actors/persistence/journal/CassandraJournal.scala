package zio.actors.persistence.journal

import com.datastax.driver.core.utils.UUIDs
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import com.fasterxml.jackson.databind.{ DeserializationFeature, ObjectMapper }
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import zio.actors.persistence.PersistenceId.PersistenceId
import zio.{ Promise, Runtime, Task, Unsafe, ZIO }

import scala.concurrent.ExecutionContext

final class CassandraJournal[Ev](
    db: CqlClient,
    numberOfShards: Int,
    enableSeqNbr: Boolean
)(implicit trace: zio.Trace)
    extends Journal[Ev] {

  private val mapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper
      .registerModule(DefaultScalaModule)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY)
  }

  override def persistEvent(persistenceId: PersistenceId, event: Ev): Task[Unit] = {
    val entityId = persistenceId.value
    val shardId  = getShardId(entityId)
    journal[Ev](persistenceId.value, shardId, event)
  }

  override def getEvents(persistenceId: PersistenceId): Task[Seq[Ev]] = {
    val entityId = persistenceId.value
    val shardId  = getShardId(entityId)
    recovery[Ev](entityId, shardId)
  }

  // same strategy used by shardcake in Sharding.getShardId
  private def getShardId: String => Long =
    entityId => math.abs(entityId.hashCode % numberOfShards) + 1

  private def journal[A](persistence_id: String, shardId: Long, event: A): Task[Unit] = {
    for {
      sequenceNr <-
        if (enableSeqNbr) {
          // REVIEW(SN): this can be problematic, thus we need to find a better workaround
          ZIO.fromFuture { implicit ec =>
            db.maxBy(persistence_id, shardId, _.sequence_nr)
              .map(_.getOrElse(0L))
          }
        } else {
          ZIO.succeed(0L)
        }
      now   = UUIDs.timeBased()
      bytes = mapper.writeValueAsBytes(event)
      message = CqlClient.Messages(
        persistence_id,
        event = bytes,
        sequence_nr = sequenceNr + 1,
        timestamp = now,
        partition_nr = shardId
      )
      _ <- ZIO.fromFuture(_ => db.insertEvent(message))
    } yield ()
  }

  private def recovery[A](persistence_id: String, shardId: Long): Task[List[A]] = {
    ZIO
      .fromFuture(_ => db.readEvents(persistence_id, shardId))
      .map { messages =>
        messages.map { message =>
          val a = mapper.readValue(message.event, new TypeReference[A] {})
          a
        }
      }
  }
}

object CassandraJournal extends JournalFactory {
  private lazy val runtime = Runtime.default
  private lazy val transactorPromise =
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(Promise.make[Exception, ExecutionContext]).getOrThrowFiberFailure()
    }

  override def getJournal[Ev](actorSystemName: String, configStr: String): Task[Journal[Ev]] = {
    val cleanConfigStr = configStr.replace(s"$actorSystemName.zio.actors.persistence", "")
    for {
      tnx       <- getTransactor
      cqlConfig <- CqlConfig.fromString(cleanConfigStr)
      db = CqlClient(cqlConfig)(tnx)
    } yield new CassandraJournal(db, cqlConfig.numberOfShards, cqlConfig.enableAutoIncrSeqNbr)
  }

  private def makeTransactor: ZIO[Any, Throwable, ExecutionContext] =
    ZIO.runtime[Any].flatMap { implicit rt =>
      for {
        transactEC <- ZIO.blockingExecutor.map(_.asExecutionContext)
      } yield transactEC
    }

  private def getTransactor: Task[ExecutionContext] =
    transactorPromise.poll.flatMap {
      case Some(value) => value
      case None =>
        for {
          newTnx <- makeTransactor
          _      <- transactorPromise.succeed(newTnx)
        } yield newTnx
    }
}
