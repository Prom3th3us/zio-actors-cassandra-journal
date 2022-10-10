import sbt._

object Dependencies {
  object Versions {
    val typeSafeConfig     = "1.4.2"
    val scalaTest          = "3.2.13"
    val logback            = "1.2.11"
    val logbackEncoder     = "7.2"
    val jacksonScalaModule = "2.13.3"
    val zio                = "2.0.2"
    val zioActors          = "0.1.0"
    val zioTest            = "2.0.0"
    val zioGrpc            = "0.6.0-test4"
    val zioConfig          = "2.0.9"
    val quill              = "4.4.1"
    val cassandraDatastax  = "3.11.3"
    val jackson            = "2.14.0-rc1"
    val jacksonDataTypes   = "2.8.0"
  }

  object TypeSafe {
    val config = "com.typesafe" % "config" % Versions.typeSafeConfig

    val all = Seq(
      config
    )
  }

  object Testing {
    val scalaTest =
      "org.scalatest" %% "scalatest" % Versions.scalaTest % Test

    val all = Seq(
      scalaTest
    )
  }

  object Logging {
    val logback = "ch.qos.logback" % "logback-classic" % Versions.logback
    val logbackEncoder =
      "net.logstash.logback" % "logstash-logback-encoder" % Versions.logbackEncoder % Runtime
    val jacksonScalaModule =
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % Versions.jacksonScalaModule % Runtime

    val all = Seq(
      logback,
      logbackEncoder,
      jacksonScalaModule
    )
  }

  object Zio {
    val zio                  = "dev.zio" %% "zio"                    % Versions.zio
    val zioActors            = "dev.zio" %% "zio-actors"             % Versions.zioActors
    val zioActorsPersistence = "dev.zio" %% "zio-actors-persistence" % Versions.zioActors
    val zioTest              = "dev.zio" %% "zio-test"               % Versions.zioTest % Test
    val zioConfig            = "dev.zio" %% "zio-config"             % Versions.zioConfig
    val zioTypesafe          = "dev.zio" %% "zio-config-typesafe"    % Versions.zioConfig

    val all = Seq(
      zio,
      zioActors,
      zioActorsPersistence,
      zioTest,
      zioConfig,
      zioTypesafe
    )
  }

  object Quill {
    val quill = "io.getquill" %% "quill-cassandra" % Versions.quill

    val all = Seq(
      quill
    )
  }

  object Cassandra {
    val cassandraDatastax = "com.datastax.cassandra" % "cassandra-driver-core" % Versions.cassandraDatastax

    val all = Seq(
      cassandraDatastax
    )
  }

  object Jackson {
    val jackson = "com.fasterxml.jackson.module" %% "jackson-module-scala" % Versions.jackson

    val all = Seq(
      jackson
    )
  }
}
