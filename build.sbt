name := "tprober_bot"

version := "1.0"

lazy val `tprober_bot` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(jdbc, cache, "org.postgresql" % "postgresql" % "9.4-1201-jdbc41", ws, specs2 % Test)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.0.0-RC2"
