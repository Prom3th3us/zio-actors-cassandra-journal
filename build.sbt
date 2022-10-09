// Global Settings
ThisBuild / scalaVersion    := "2.13.8"
ThisBuild / versionScheme   := Some("early-semver")
ThisBuild / dynverSeparator := "-"
ThisBuild / conflictManager := ConflictManager.latestRevision
ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")
ThisBuild / scalacOptions ++= Seq("-Ymacro-annotations", "-target:jvm-17")

inThisBuild(
  List(
    organization := "io.github.prom3th3us",
    homepage     := Some(url("https://github.com/prom3th3us/zio-actors-cassandra-journal")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "ffakenz",
        "Franco Testagrossa",
        "franco.testagrossa@gmail.com",
        url("https://github.com/ffakenz")
      )
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
  )
)

import Settings._

lazy val root = (project in file("."))
  .settings(skip / publish := true)
  .disablePlugins(CiReleasePlugin)
  .settings(CommandAliases.aliases)
  .aggregate(journal, example)

lazy val example = project
  .settings(skip / publish := true)
  .disablePlugins(CiReleasePlugin)
  .settings(commonSettings, scalafixSettings)
  .dependsOn(journal)

lazy val journal = project
  .settings(
    name := "zio-actors-cassandra-journal"
  )
  .settings(commonSettings, scalafixSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.Zio.all,
      Dependencies.Quill.all,
      Dependencies.Cassandra.all,
      Dependencies.Jackson.all
    ).flatten
  )
  .enablePlugins(AshScriptPlugin)
  .enablePlugins(CiReleasePlugin)
