package com.pendula.rigby

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.blaze.server._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  def logAndDie(e: Throwable): IO[ExitCode] = {
    Predef.println("\nStartup failed\n")
    Predef.println(e)
    IO.pure(ExitCode.Success)
  }

  def runServer(routes: HttpRoutes[IO]) =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

  def run(args: List[String]): IO[ExitCode] = {
    Service.createService().fold(
      logAndDie,
      runServer
    )
  }
}
