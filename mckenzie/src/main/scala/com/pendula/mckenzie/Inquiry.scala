package com.pendula.mckenzie

import io.circe._
import io.circe.generic.semiauto._

final case class Inquiry(
  firstName: String,
  lastName: String,
  email: String,
  phone: String,
  postcode: String
)

object Inquiry {
  implicit val decoder: Decoder[Inquiry] = deriveDecoder[Inquiry]
  implicit val encoder: Encoder[Inquiry] = deriveEncoder[Inquiry]
}
