import com.pendula.mckenzie.Inquiry
import Generators.genInquiry
import com.pendula.mckenzie.Mailer.{
  acceptedTemplate,
  declinedTemplate,
  generateTemplate
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class PropertyTests
    extends AnyPropSpec
    with Matchers
    with ScalaCheckDrivenPropertyChecks {
  // Validate that anything starting with NW8 gets the accepted template
  // and anything else gets denied
  property("NW8 Postcode property") {
    forAll(genInquiry) { inquiry: Inquiry =>
      if (inquiry.postcode.startsWith("NW8")) {
        generateTemplate(inquiry) should be(
          acceptedTemplate(inquiry.firstName, inquiry.lastName)
        )
      } else {
        generateTemplate(inquiry) should be(
          declinedTemplate(inquiry.firstName, inquiry.lastName)
        )
      }
    }
  }

}
