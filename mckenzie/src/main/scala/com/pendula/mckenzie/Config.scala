package com.pendula.mckenzie

import com.pendula.mckenzie.Mailer.MailerClient
import software.amazon.awssdk.services.sqs.SqsClient

case class Config(client: SqsClient, queueUrl: String, mailer: MailerClient)

object Config {

  def createConfig(): Either[Throwable, Config] = for {
      client <- SQS.createClient()
      baseUrl <- SQS.getQueue(client)
    } yield {
      Config(client, baseUrl, Mailer.createMailer())
    }
}
