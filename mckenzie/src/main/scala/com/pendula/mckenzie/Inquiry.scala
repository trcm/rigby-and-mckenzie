package com.pendula.mckenzie

import io.circe._
import io.circe.generic.semiauto._

//TODO: Refactor this into a module shared between Rigby and McKenzie
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
