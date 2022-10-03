// Global Settings
ThisBuild / scalaVersion    := "2.13.8"
ThisBuild / organization    := "Prom3theus"
ThisBuild / versionScheme   := Some("early-semver")
ThisBuild / dynverSeparator := "-"
ThisBuild / conflictManager := ConflictManager.latestRevision
ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")
ThisBuild / scalacOptions ++= Seq("-Ymacro-annotations", "-target:jvm-17")
ThisBuild / publish / skip := true // TODO

lazy val commonSettings = Seq(
  run / fork                := true,
  Test / testForkedParallel := true,
  libraryDependencies ++= Seq(
    Dependencies.Logging.all,
    Dependencies.TypeSafe.all,
    Dependencies.Testing.all
  ).flatten
)

lazy val scalafixSettings: Seq[Setting[_]] = Seq(
  addCompilerPlugin(scalafixSemanticdb),
  semanticdbEnabled := true,
  scalafixOnCompile := true
)

lazy val dockerSettings = Seq(
  dockerUsername              := sys.props.get("docker.username"),
  dockerRepository            := sys.props.get("docker.registry"),
  Docker / version            := "latest",
  Docker / organization       := "prom3theus",
  Docker / dockerBaseImage    := "openjdk",
  Docker / packageName        := "prom3theus/zio-actors-cassandra-journal",
  Docker / dockerExposedPorts := Seq(9042)
)

lazy val root = (project in file("."))
  .settings(
    name := "zio-actors-cassandra-journal"
  )
  .aggregate(zio)

lazy val zio = project
  .settings(
    name := "zio"
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
  .settings(dockerSettings)
  .enablePlugins(DockerPlugin, AshScriptPlugin)
