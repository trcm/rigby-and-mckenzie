package com.pendula.rigby

import cats.effect.IO
import com.pendula.rigby.SQS.MessageSender
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.{HttpRoutes, MediaType, Request, Response}
import org.slf4j.{Logger, LoggerFactory}
import org.typelevel.ci.CIStringSyntax

object Service {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def handleResponse(
      messageQueueResponse: Either[Throwable, Unit]
  ): IO[Response[IO]] =
    messageQueueResponse.fold(
      e => {
        logger.debug(s"Failed to send message to queue $e")
        // At this point, we've tried to send the message to the queue
        // but the queue did not receive it as expected.
        BadGateway()
      },
      _ => Accepted()
    )

  // Ideally this would send back more information about "why" this failed.
  // The default error from Circe is not very helpful.
  def respondToParseFailure(): IO[Response[IO]] = BadRequest()

  def sendToQueue(sender: MessageSender)(inquiry: Inquiry): IO[Response[IO]] = for {
    response <- IO.pure(sender(inquiry))
    resp <- handleResponse(response)
  } yield resp

  def checkContentType(request: Request[IO]): Boolean = request.headers.get[`Content-Type`].fold(
    false)(
    (mt: `Content-Type`) =>  mt.mediaType.equals(MediaType.application.json)
  )

  def rigbyService(sender: MessageSender): HttpRoutes[IO] =
    HttpRoutes.of[IO] { case req @ POST -> Root / "hook" if checkContentType(req) =>
      req.as[Inquiry].attempt.flatMap(parsedJson => parsedJson.fold(
        _ => respondToParseFailure(),
        sendToQueue(sender)
      ))
    case req @ POST -> Root / "hook" => BadRequest()
    }

  def createService(): Either[Throwable, HttpRoutes[IO]] = for {
    client <- SQS.createClient()
    queueUrl <- SQS.getQueue(client)
  } yield {
    val messageSender = (s: Inquiry) => SQS.sendMessage(logger, client, queueUrl)(s)
    rigbyService(messageSender)
  }

}
