package com.pendula.mckenzie

import cats.effect.{ExitCode, IO}

object Service {

  def parsePostcode(inquiry: Inquiry) = {

  }

  def runService(config: Config): IO[ExitCode] = {
    // do this forever?
    while (true) {
      val messages = SQS.getMessages(config.client, config.queueUrl)

      // Handle errors
      Mailer.processAndSendMail(messages)
    }
    IO.pure(ExitCode.Success)

  }

}
