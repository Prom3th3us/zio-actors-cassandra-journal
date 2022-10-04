package zio.actors.persistence.journal

import com.typesafe.config.ConfigFactory
import com.typesafe.config.{ Config => TypeSafeConfig }
import io.getquill.CassandraContextConfig

import CqlConfig._

case class CqlConfig(
    numberOfShards: Int,
    enableAutoIncrSeqNbr: Boolean,
    keyspace: String,
    preparedStatementCacheSize: Int,
    session: CqlSession
)

object CqlConfig {
  sealed trait CqlConsistencyLevel {
    def asString: String
  }

  object CqlConsistencyLevel {
    case object ONE          extends CqlConsistencyLevel { val asString = "ONE"          }
    case object LOCAL_QUORUM extends CqlConsistencyLevel { val asString = "LOCAL_QUORUM" }
    def fromString(str: String): Option[CqlConsistencyLevel] =
      str match {
        case ONE.asString          => Some(ONE)
        case LOCAL_QUORUM.asString => Some(LOCAL_QUORUM)
        case _                     => None
      }
  }

  case class CqlQueryOptions(
      consistencyLevel: CqlConsistencyLevel
  )

  case class CqlSession(
      contactPoint: String,
      queryOptions: CqlQueryOptions
  )

  def default: CqlConfig =
    CqlConfig(
      numberOfShards = 300,
      enableAutoIncrSeqNbr = true,
      keyspace = "event_sourcing",
      preparedStatementCacheSize = 100,
      session = CqlSession(
        contactPoint = "0.0.0.0",
        queryOptions = CqlQueryOptions(
          consistencyLevel = CqlConsistencyLevel.ONE
        )
      )
    )

  def fromTypesafe(cql: TypeSafeConfig): CqlConfig = {
    val numberOfShards             = cql.getInt("numberOfShards")
    val enableAutoIncrSeqNbr       = cql.getBoolean("enableAutoIncrSeqNbr")
    val db                         = cql.getConfig("db")
    val keyspace                   = db.getString("keyspace")
    val preparedStatementCacheSize = db.getInt("preparedStatementCacheSize")
    val session                    = db.getConfig("session")
    val contactPoint               = session.getString("contactPoint")
    val queryOptions               = session.getConfig("queryOptions")
    val consistencyLevel           = queryOptions.getString("consistencyLevel")
    CqlConfig(
      numberOfShards = numberOfShards,
      enableAutoIncrSeqNbr = enableAutoIncrSeqNbr,
      keyspace = keyspace,
      preparedStatementCacheSize = preparedStatementCacheSize,
      session = CqlSession(
        contactPoint = contactPoint,
        queryOptions = CqlQueryOptions(
          consistencyLevel = CqlConsistencyLevel.fromString(consistencyLevel).get
        )
      )
    )
  }
  def toClientConfig(config: CqlConfig): TypeSafeConfig = ConfigFactory.parseString(
    s"""
    |keyspace=${config.keyspace}
    |preparedStatementCacheSize=${config.preparedStatementCacheSize}
    |session.contactPoint=${config.session.contactPoint}
    |session.queryOptions.consistencyLevel=${config.session.queryOptions.consistencyLevel.asString}    
    """.stripMargin
  )
}
