import com.pendula.mckenzie.Inquiry
import com.pendula.mckenzie.Mailer.{acceptedTemplate, declinedTemplate, generateTemplate}
import org.scalatest.wordspec.AnyWordSpec

class UnitTests extends AnyWordSpec {

  val testFirstName = "John"
  val testLastName = "Doe"
  def inquiry(postCode: String): Inquiry = Inquiry(
    firstName = testFirstName,
    lastName = testLastName,
    email = "johndoe@fake.com",
    phone = "1234 5678",
    postcode = postCode
  )


  "A Postcode" when {
    "in the NW8 region" should {
      "Return the accepted template" in {
        val validInquiry = inquiry("NW8 9AY")
        assert(generateTemplate(validInquiry) ==
          acceptedTemplate(validInquiry.firstName, validInquiry.lastName)
        )
      }
    }

    "not in the NW8 region" should {
      "Return the declined template" in {
        val invalidInquiry = inquiry("L25 GEJ")
        assert(generateTemplate(invalidInquiry) ==
          declinedTemplate(invalidInquiry.firstName, invalidInquiry.lastName)
        )
      }
    }

  }

}
