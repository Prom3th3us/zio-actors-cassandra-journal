import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin.autoImport._

object Settings extends CommonScalac {
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
}
