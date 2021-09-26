package com.pendula.mckenzie

import com.pendula.mckenzie.Mailer.MailerClient
import org.slf4j.Logger
import software.amazon.awssdk.services.sqs.SqsClient

case class Config(client: SqsClient, queueUrl: String, mailer: MailerClient, logger: Logger)

object Config {

  def createConfig(logger: Logger): Either[Throwable, Config] = for {
      client <- SQS.createClient()
      baseUrl <- SQS.getQueue(client)
    } yield {
      Config(client, baseUrl, Mailer.sendMail, logger)
    }
}
