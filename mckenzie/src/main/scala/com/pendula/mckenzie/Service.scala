package com.pendula.mckenzie

import cats.effect.{ExitCode, IO}

object Service {

  def runService(config: Config): IO[ExitCode] = {
    while (true) {
      config.logger.info("Polling for messages...")
      val messages = SQS.getMessages(config.logger, config.client, config.queueUrl)
      config.logger.info(s"Recieved ${messages.size} messages")

      // TODO: Handle errors
      Mailer.processAndSendMail(config.mailer, messages)
    }
    IO.pure(ExitCode.Success)

  }

}
