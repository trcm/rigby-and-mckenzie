package com.pendula.rigby

import cats.implicits._
import io.circe.syntax._
import software.amazon.awssdk.auth.credentials.{
  AwsCredentials,
  EnvironmentVariableCredentialsProvider,
  StaticCredentialsProvider
}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{
  GetQueueUrlRequest,
  SendMessageRequest,
  SendMessageResponse
}
import java.net.URI

object SQS {

  type MessageSender = Inquiry => Either[Throwable, Unit]
  val queueName: String = "church.fifo"

  def getQueue(client: SqsClient): Either[Throwable, String] =
    Either.catchNonFatal(
      client
        .getQueueUrl(
          GetQueueUrlRequest.builder().queueName(queueName).build()
        )
        .queueUrl()
    )

  def createClient(): Either[Throwable, SqsClient] =
    Either.catchNonFatal(
      SqsClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .endpointOverride(URI.create("http://localhost:4566"))
        .build()
    )

  def sendMessage(client: SqsClient, queueUrl: String)(
      message: Inquiry
  ): Either[Throwable, Unit] = {
    val messageResponse = Either.catchNonFatal(
      client.sendMessage(
        SendMessageRequest
          .builder()
          .queueUrl(queueUrl)
          .messageBody(message.asJson.toString())
          .messageGroupId("1")
          .build()
      )
    )
    // We're only interested in failure
    messageResponse.map(_ => ())
  }
}
