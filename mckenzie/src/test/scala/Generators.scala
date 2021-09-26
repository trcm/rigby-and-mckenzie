import com.pendula.mckenzie.Inquiry
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

object Generators {

  // UK postcode specification

  // Assuming we will only handle UK postcodes then these are the
  // possible options where A represents a letter and 9 represents a
  // number.
  // Taken from https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom#Formatting
  // AA9A 9AA
  // A9A 9AA
  // A9 9AA
  // A99 9AA
  // AA9 9AA
  // AA99 9AA

  def genStringN(n: Int): Gen[String] =
    Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString(""))

  def genPostCodeSet(n: Int): Gen[String] = genStringN(n)

  def genAcceptedPostcode(): Gen[String] = for {
    // UK postcodes that have the postcode starting with NW8 will only
    // have three characters as the remaining part of the code
    secondSet <- genPostCodeSet(3)
  } yield {
    s"NW8 $secondSet"
  }

  def genInvalidPostcode(): Gen[String] = for {
    firstSet <- arbitrary[String] suchThat (s =>
      !s.equals("NW8") && (s.length >= 2 && s.length <= 4)
    )
    secondSet <- genPostCodeSet(3)
  } yield {
    s"$firstSet $secondSet"
  }

  def genPostcode(): Gen[String] =
    Gen.oneOf(genAcceptedPostcode(), genInvalidPostcode())

  def genInquiry: Gen[Inquiry] = for {
    firstName <- arbitrary[String].suchThat(_.nonEmpty)
    lastName <- arbitrary[String].suchThat(_.nonEmpty)
    // There is no validation on phone or email currently
    // This is because we're assuming they're coming from a CRM
    email <- genStringN(3)
    emailDomain <- genStringN(5)
    phone <- Gen.listOfN(8, Gen.numChar).map(_.mkString(""))
    postCode <- genPostcode()
  } yield Inquiry(
    firstName,
    lastName,
    s"${email}@${emailDomain}.com",
    phone,
    postCode
  )
}
