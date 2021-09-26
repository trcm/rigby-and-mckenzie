package com.pendula.mckenzie

object Mailer {

  type MailerClient = (Inquiry, String) => Either[Throwable, String]

  //TODO: We should use a more fully featured templating solution in production.
  def acceptedTemplate(firstName: String, lastName: String) = s"""
Dear $firstName $lastName,

Thank you for your inquiry, someone will contact you shortly.

Always remember:

There once was a man from Nantucket.
Who kept all his cash in a bucket.
But his daughter, named Nan, Ran away with a man,
And as for the bucket, Nantucket.

Regards,
Father McKenzie
"""

  def declinedTemplate(firstName: String, lastName: String) = s"""
Dear $firstName $lastName,

Thank you for inquiry,
but we have to decline most politely,
Your postcode was wrong,
So we'll say so long,
but thank you for your inquiry

Regards,
Father McKenzie
"""

  // We're only interested in postcodes starting with NW8, this assumes that the
  // data is formatted correctly in the UK postcode format.
  def generateTemplate(inquiry: Inquiry): String = inquiry.postcode.take(3) match {
    case "NW8" => acceptedTemplate(inquiry.firstName, inquiry.lastName)
    case _ => declinedTemplate(inquiry.firstName, inquiry.lastName)
  }

  // Ideally this would wrap the throwable in an error type defined in the service
  def processAndSendMail(mailer: MailerClient, messages: List[Inquiry]): List[Either[Throwable, String]] = {
    val emails = messages.map(i => {
      (i, generateTemplate(i))
    })

    emails.map({
      case (inquiry, template) => mailer(inquiry, template)
    })
  }

  def sendMail(inquiry: Inquiry, template: String): Either[Throwable, String] = {
    // Ideally we'd be catching errors from the email sending, but we'll
    // assume they all pass
    println(s"Sending email to ${inquiry.email}")
    println(template)

    Thread.sleep(500)

    Right("Sent")
  }

}
