package zio.actors.persistence.journal

import com.typesafe.config.{ ConfigFactory, Config => TypeSafeConfig }
import zio._
import zio.config._
import ConfigDescriptor._
import zio.actors.persistence.journal.CqlConfig._
import zio.config.typesafe._

import scala.util.{ Failure, Success, Try }

case class CqlConfig(
    numberOfShards: Int,
    enableAutoIncrSeqNbr: Boolean,
    db: Db
)

object CqlConfig {
  sealed trait ConsistencyLevel
  object ConsistencyLevel {
    case object ONE          extends ConsistencyLevel
    case object LOCAL_QUORUM extends ConsistencyLevel
    def fromString(str: String): Try[ConsistencyLevel] =
      str match {
        case "ONE"          => Success(ONE)
        case "LOCAL_QUORUM" => Success(LOCAL_QUORUM)
        case unknown        => Failure(new IllegalArgumentException(s"Unknown ConsistencyLevel: $unknown"))
      }
  }

  case class QueryOptions(
      consistencyLevel: ConsistencyLevel
  )

  case class Session(
      contactPoint: String,
      queryOptions: QueryOptions
  )

  case class Db(
      keyspace: String,
      preparedStatementCacheSize: Int,
      session: Session
  )

  def default: CqlConfig =
    CqlConfig(
      numberOfShards = 300,
      enableAutoIncrSeqNbr = true,
      db = Db(
        keyspace = "event_sourcing",
        preparedStatementCacheSize = 100,
        session = Session(
          contactPoint = "0.0.0.0",
          queryOptions = QueryOptions(
            consistencyLevel = ConsistencyLevel.ONE
          )
        )
      )
    )

  private val queryOptionsDescriptor: ConfigDescriptor[QueryOptions] =
    string("consistencyLevel")(
      str => ConsistencyLevel.fromString(str).map(QueryOptions.apply).get,
      queryOptions => Some(queryOptions.consistencyLevel.toString)
    )

  private val sessionDescriptor: ConfigDescriptor[Session] =
    (string("contactPoint") <*> nested("queryOptions")(queryOptionsDescriptor))(
      { case (contactPoint, queryOptions) => Session(contactPoint, queryOptions) },
      Session.unapply
    )

  private val dbConfigDescriptor: ConfigDescriptor[Db] =
    (string("keyspace") <*>
      int("preparedStatementCacheSize") <*>
      nested("session")(sessionDescriptor))(
      { case ((str, int), session) => Db(str, int, session) },
      db => Some(db.keyspace -> db.preparedStatementCacheSize -> db.session)
    )

  private val configDescriptor: ConfigDescriptor[CqlConfig] =
    (int("numberOfShards") <*>
      boolean("enableAutoIncrSeqNbr") <*>
      nested("db")(dbConfigDescriptor))(
      { case ((int, bool), db) => CqlConfig(int, bool, db) },
      cqlConfig => Some(cqlConfig.numberOfShards -> cqlConfig.enableAutoIncrSeqNbr -> cqlConfig.db)
    )

  def fromString(configStr: String): Task[CqlConfig] =
    read(
      CqlConfig.configDescriptor from
        ConfigSource.fromHoconString(configStr)
    )

  def toClientConfig(config: CqlConfig): TypeSafeConfig = ConfigFactory.parseString(
    s"""
    |keyspace=${config.db.keyspace}
    |preparedStatementCacheSize=${config.db.preparedStatementCacheSize}
    |session.contactPoint=${config.db.session.contactPoint}
    |session.queryOptions.consistencyLevel=${config.db.session.queryOptions.consistencyLevel.toString}
    """.stripMargin
  )
}
