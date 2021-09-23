package com.pendula.rigby

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.blaze.server._
import org.http4s.dsl.io._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  type MessageSender = String => String

  def logAndDie(e: Throwable): IO[ExitCode] = {
    Predef.println("Failed to startup")
    Predef.println(e)
    IO.pure(ExitCode.Error)
  }

  def rigbyService(sender: MessageSender) =
    HttpRoutes.of[IO] { case req @ POST -> Root / "hook" =>
      for {
//        inquiry <- req.as[Inquiry]
        // send this to sqs, if ok then send it back send ok
        _ <- IO.pure(sender("thing"))
        resp <- Ok()
      } yield resp
    }

  def createService() = for {
      client <- SQS.createClient()
      queueUrl <- SQS.getQueue(client)
    } yield {
      val messageSender = (s: String) => SQS.sendMessage(client, queueUrl)(s)
      rigbyService(messageSender)
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
    createService().fold(
      logAndDie,
      runServer
    )
  }
}
