package zio.actors.persistence.journal

import com.typesafe.config.{ Config, ConfigFactory, ConfigRenderOptions }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import zio._

class CqlConfigSpec extends AsyncWordSpec with Matchers {
  import CqlConfigSpec._

  "CqlConfig" should {
    "parse from typesafe to cql client config" in {
      val cqlConfig = parseCqlConfig(configStr)
      cqlConfig should be(CqlConfig.default)
    }
    "convert to cql client as typesafe config" in {
      val cqlConfig = parseCqlConfig(configStr)
      CqlConfig.toClientConfig(cqlConfig) should be(typesafeConfig.getConfig("db"))
    }
  }
}

object CqlConfigSpec {
  private val configPath                           = "GuildSystem.zio.actors.persistence"
  private val typesafeConfig                       = ConfigFactory.load().getConfig(configPath)
  private val showTypesafeConfig: Config => String = _.root().render(ConfigRenderOptions.concise())
  private val configStr                            = showTypesafeConfig(typesafeConfig)
  private val runtime                              = Runtime.default
  private val parseCqlConfig: String => CqlConfig = str =>
    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe
        .run(CqlConfig.fromString(str))
        .getOrThrowFiberFailure()
    }
}
