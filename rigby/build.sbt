import scala.sys.process._

name := "rigby"

version := "0.1"

scalaVersion := "2.13.6"

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
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-client"  % http4sVersion,
  "software.amazon.awssdk" % "sqs" % "2.17.43",
  "org.typelevel" %% "cats-effect" % "2.5.3"
)

enablePlugins(DockerPlugin)

docker / dockerfile := {
  // Pulled from the sbt-docker help
  val jarFile: File = (Compile / packageBin / sbt.Keys.`package`).value
  val classpath = (Compile / managedClasspath).value
  val mainclass = (Compile / packageBin / mainClass).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget

  new Dockerfile {
    from("openjdk:8-jre-alpine")
    add(classpath.files, "/app/")
    add(jarFile, jarTarget)
    // these are hard coded but should be configurable
    env("AWS_ACCESS_KEY_ID" -> "test", "AWS_SECRET_ACCESS_KEY" -> "test")
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

docker / imageNames := Seq{
  ImageName(
    repository = name.value,
    // rev-parse HEAD has a newline at the end
    tag = Some("git rev-parse HEAD".!!.trim())
  )
}
