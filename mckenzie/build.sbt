name := "mckenzie"

version := "0.1"

scalaVersion := "2.13.6"

idePackagePrefix := Some("org.pendula")

val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val http4sVersion = "0.22.5"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.6",
  "org.slf4j" % "slf4j-api" % "1.7.32",
  "org.typelevel" %% "cats-core" % "2.3.0",
  "org.typelevel" %% "cats-effect" % "2.5.3",
  "software.amazon.awssdk" % "sqs" % "2.17.43",
  "org.typelevel" %% "cats-effect" % "2.5.3"
)
