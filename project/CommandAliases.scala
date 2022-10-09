import sbt.{ addCommandAlias, Def, State }

object CommandAliases {
  // Command Aliases
  val aliases: Seq[Def.Setting[State => State]] = Seq(
    addCommandAlias("c", "compile"),
    addCommandAlias("tc", "test:compile"),
    addCommandAlias("ca", ";c;tc"),
    addCommandAlias("cd", "project"),
    addCommandAlias("ls", "projects"),
    addCommandAlias("to", "testOnly *"),
    addCommandAlias("ft", "scalafmt"),
    addCommandAlias("tft", "test:scalafmt"),
    addCommandAlias("fmt", ";ft;tft"),
    addCommandAlias("ftc", "scalafmtCheck"),
    addCommandAlias("tftc", "test:scalafmtCheck"),
    addCommandAlias("tfmt", ";ftc;tftc"),
    addCommandAlias("prep", ";clean;ca;fmt;tfmt")
  ).flatten
}
