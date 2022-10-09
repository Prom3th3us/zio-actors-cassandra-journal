package zio.actors.persistence.journal

import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import zio.actors.persistence.journal.CqlConfig

class CqlConfigSpec extends AsyncWordSpec with Matchers {

  "CqlConfig" should {
    val configPath = "GuildSystem.zio.actors.persistence"
    "parse from typesafe to cql client config" in {
      val typesafeConfig = ConfigFactory.load().getConfig(configPath)
      val cqlConfig      = CqlConfig.fromTypesafe(typesafeConfig)
      cqlConfig should be(CqlConfig.default)
    }
    "convert to cql client as typesafe config" in {
      val typesafeConfig = ConfigFactory.load().getConfig(configPath)
      val cqlConfig      = CqlConfig.fromTypesafe(typesafeConfig)
      CqlConfig.toClientConfig(cqlConfig) should be(typesafeConfig.getConfig("db"))
    }
  }
}
