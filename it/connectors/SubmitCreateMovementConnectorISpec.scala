package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.SubmitCreateMovementConnector
import fixtures.BaseFixtures
import models.requests.{DataRequest, UserRequest}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class SubmitCreateMovementConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  implicit lazy val dr: DataRequest[_] =
    DataRequest(UserRequest(FakeRequest(), testErn, "", "", "", false), testDraftId, emptyUserAnswers, testMinTraderKnownFacts)

  private lazy val connector: SubmitCreateMovementConnector = app.injector.instanceOf[SubmitCreateMovementConnector]

  ".submit" - {

    val url = s"/emcs-tfe/create-movement/$testErn/$testDraftId"
    val requestBody = Json.toJson(minimumSubmitCreateMovementModel)
    val responseBody = Json.toJson(minimumSubmitCreateMovementResponse)

    "must return true when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(responseBody)))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Right(minimumSubmitCreateMovementResponse)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }


}
