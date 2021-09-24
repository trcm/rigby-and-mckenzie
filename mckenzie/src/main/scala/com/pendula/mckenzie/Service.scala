package com.pendula.mckenzie

import cats.effect.{ExitCode, IO}

object Service {

  def parsePostcode(inquiry: Inquiry) = {

  }

  def runService(config: Config): IO[ExitCode] = {
    // do this forever?
    while (true) {
      println("Polling for messages...")
      val messages = SQS.getMessages(config.client, config.queueUrl)
      println(s"Recieved ${messages.size} messages")
      // Handle errors
      Mailer.processAndSendMail(messages)
    }
    IO.pure(ExitCode.Success)

  }

}
