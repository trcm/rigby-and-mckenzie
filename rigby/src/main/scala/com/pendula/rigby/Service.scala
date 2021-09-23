package com.pendula.rigby

import cats.effect.IO
import com.pendula.rigby.SQS.MessageSender
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Response}

object Service {

  def handleResponse(
      messageQueueResponse: Either[Throwable, Unit]
  ): IO[Response[IO]] =
    messageQueueResponse.fold(
      (e) => {
        Predef.println("Failed to send message to queue")
        Predef.println(e)
        // At this point, we've tried to send the message to the queue
        // but the queue did not receieve it as expected.
        BadGateway()
      },
      _ => Accepted()
    )

  def rigbyService(sender: MessageSender) =
    HttpRoutes.of[IO] { case req @ POST -> Root / "hook" =>
      for {
        inquiry <- req.as[Inquiry]
        response <- IO.pure(sender(inquiry))
        resp <- handleResponse(response)
      } yield resp
    }

  def createService() = for {
    client <- SQS.createClient()
    queueUrl <- SQS.getQueue(client)
  } yield {
    val messageSender = (s: Inquiry) => SQS.sendMessage(client, queueUrl)(s)
    rigbyService(messageSender)
  }

}
