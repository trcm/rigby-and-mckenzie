package com.pendula.mckenzie

import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{
  DeleteMessageBatchRequest,
  DeleteMessageRequest,
  GetQueueUrlRequest,
  Message,
  ReceiveMessageRequest,
  ReceiveMessageResponse
}
import cats.implicits._
import io.circe.parser.decode
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region

import java.net.URI
import scala.jdk.CollectionConverters._

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

  def getMessages(client: SqsClient, queueUrl: String): List[Inquiry] = {
    // TODO: Handle errors here
    val messages: ReceiveMessageResponse = client.receiveMessage(
      ReceiveMessageRequest
        .builder()
        .queueUrl(queueUrl)
        // long poll
        .waitTimeSeconds(20)
        // hide the messages from subsequent
        // receive calls to give us time to pop them from the queue
        // and process them
        .visibilityTimeout(30)
        .build()
    )
    val m: List[Message] = messages.messages().asScala.toList
    // We care about decode failures, partition them so we can log them
    val t =
      m.map(mprime => decode[Inquiry](mprime.body())).partitionMap(identity)

    val inquires = t match {
      case (errors, inquires) =>
        // TODO: Use a proper logger and log these failures.
        if (errors.nonEmpty) {
          println("Error parsing messages")
          println(errors)
        }
        inquires
    }

    // Delete the message so its not processed again.
    // Errors are treated as invalid requests and will be deleted.
    // TODO: Handle errors during deletion
    m.map(message => {
      client.deleteMessage(
        DeleteMessageRequest
          .builder()
          .queueUrl(queueUrl)
          .receiptHandle(message.receiptHandle())
          .build()
      )
    })

    inquires
  }
}