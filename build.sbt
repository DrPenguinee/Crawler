ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

val zioVersion     = "2.1.11"
val zioHttpVersion = "3.0.1"
val zioJson        = "0.7.3"

lazy val root = (project in file("."))
  .settings(
    name := "Crawler",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"      % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion,
      "dev.zio" %% "zio-json" % zioJson,
    ),
  )
