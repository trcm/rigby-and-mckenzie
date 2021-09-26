import com.pendula.rigby.Inquiry
import com.pendula.rigby.Service.rigbyService
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Headers, HttpRoutes, Method, Request, Response, Status}
import org.scalatest.wordspec.AnyWordSpec
import cats.effect.IO
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers

class ServiceTests extends AnyWordSpec with Matchers {

  def testMessageSender(inquiry: Inquiry): Either[Throwable, Unit] = inquiry.firstName match {
    case "Fail" => Left(new Exception("Sending failed"))
    case _ => Right(())
  }

  val testService: HttpRoutes[IO] = rigbyService(testMessageSender)

  def checkRequest(request: Request[IO], status: Status): Assertion = {
    val response: Response[IO] = testService.orNotFound.run(request).unsafeRunSync()
    response.status should be (status)
  }

  "Rigby Service" when {
    "if it receives a valid response" should {
      "Return 200 if able to submit to the queue" in {
        val request: Request[IO] = Request(method = Method.POST, uri = uri"/hook").withEntity(
          Inquiry(firstName = "John",
            lastName = "Lennon",
            email = "biggerthanjesus@beatles.com",
            phone = "1234 4312",
            postcode = "NW8 2AW",
          )
        )
        checkRequest(request, Status.Accepted)
      }
      "Return 502 if not able to submit to the queue" in {
        val request: Request[IO] = Request(method = Method.POST, uri = uri"/hook")
          .withEntity(
            Inquiry(firstName = "Fail",
              lastName = "Test",
              email = "fail@test.com",
              phone = "1234 4312",
              postcode = "NW8 2AW",
            )
          )
        checkRequest(request, Status.BadGateway)
      }
    }
    "if it receives an invalid request" should {
      "return 400" in  {
        val request: Request[IO] = Request(method = Method.POST, uri = uri"/hook").withEntity(
          "this isn't a valid request"
        )
        checkRequest(request, Status.BadRequest)
      }
      "return 400 when invalid content-type" in {
        val request: Request[IO] = Request(method = Method.POST, uri = uri"/hook")
          .withHeaders(Headers(
            ("Content-Type", "text/plain")
          )).withEmptyBody
        checkRequest(request, Status.BadRequest)
      }
    }
  }

}
