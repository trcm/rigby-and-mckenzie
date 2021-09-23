package com.pendula.rigby

import cats.effect.IO
import com.pendula.rigby.SQS.MessageSender
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Response}
import org.slf4j.{Logger, LoggerFactory}

object Service {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def handleResponse(
      messageQueueResponse: Either[Throwable, Unit]
  ): IO[Response[IO]] =
    messageQueueResponse.fold(
      (e) => {
        logger.debug(s"Failed to send message to queue ${e}")
        // At this point, we've tried to send the message to the queue
        // but the queue did not receive it as expected.
        BadGateway()
      },
      _ => Accepted()
    )

  def rigbyService(sender: MessageSender): HttpRoutes[IO] =
    HttpRoutes.of[IO] { case req @ POST -> Root / "hook" =>
      for {
        inquiry <- req.as[Inquiry]
        response <- IO.pure(sender(inquiry))
        resp <- handleResponse(response)
      } yield resp
    }

  def createService(): Either[Throwable, HttpRoutes[IO]] = for {
    client <- SQS.createClient()
    queueUrl <- SQS.getQueue(client)
  } yield {
    val messageSender = (s: Inquiry) => SQS.sendMessage(logger, client, queueUrl)(s)
    rigbyService(messageSender)
  }

}
