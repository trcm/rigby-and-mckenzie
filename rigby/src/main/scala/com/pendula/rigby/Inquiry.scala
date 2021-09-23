package com.pendula.rigby

import cats.effect._
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.circe._

final case class Inquiry(
  firstName: String,
  lastName: String,
  email: String,
  phone: String,
  postcode: String
)

object Inquiry {
  implicit val decoder = deriveDecoder[Inquiry]
  implicit val inquiryDecoder = jsonOf[IO, Inquiry]
}
