package com.pendula.rigby

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.blaze.server._
import org.http4s.implicits._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def logAndDie(e: Throwable): IO[ExitCode] = {
    logger.error(s"\nStartup failed\n $e")
    IO.pure(ExitCode.Success)
  }

  def runServer(routes: HttpRoutes[IO]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

  def run(args: List[String]): IO[ExitCode] = {
    println("Starting Rigby...")
    Service.createService().fold(
      logAndDie,
      runServer
    )
  }
}
