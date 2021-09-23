package com.pendula.rigby

import cats.implicits._
import software.amazon.awssdk.auth.credentials.{AwsCredentials, EnvironmentVariableCredentialsProvider, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest

import java.net.URI
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageResponse

object SQS {

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

  def sendMessage(client: SqsClient, queueUrl: String)(message: String): String = {
    val messageResponse: SendMessageResponse = client.sendMessage(
      SendMessageRequest
        .builder()
        .queueUrl(queueUrl)
        .messageBody(message)
        .messageGroupId("1")
        .build()
    )
    println(messageResponse.messageId())
    messageResponse.messageId()
  }
}