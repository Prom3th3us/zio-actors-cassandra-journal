// Packing
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.0")

// Linting & Styling
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.1")

// Versioning && Publishing
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")

addDependencyTreePlugin
