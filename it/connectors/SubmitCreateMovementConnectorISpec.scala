package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.SubmitCreateMovementConnector
import fixtures.BaseFixtures
import models.requests.{DataRequest, UserRequest}
import models.response.{UnexpectedDownstreamDraftSubmissionResponseError, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.ExecutionContext.Implicits.global

class SubmitCreateMovementConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures
  with LogCapturing {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  implicit lazy val dr: DataRequest[_] =
    DataRequest(UserRequest(FakeRequest(), testErn, "", "", "", false), testDraftId, emptyUserAnswers, Some(testMinTraderKnownFacts))

  private lazy val connector: SubmitCreateMovementConnector = app.injector.instanceOf[SubmitCreateMovementConnector]

  ".submit" - {

    val url = s"/emcs-tfe/create-movement/$testErn/$testDraftId"
    val requestBody = Json.toJson(minimumSubmitCreateMovementModel)
    val responseBody = successResponseEISJson

    "must return true when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(responseBody)))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Right(submitCreateMovementResponseEIS)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(NOT_FOUND))
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      withCaptureOfLoggingFrom(connector.logger) { logs =>

        connector.submit(minimumSubmitCreateMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)

        logs.exists(_.getMessage == "[SubmitCreateMovementConnector][post] Unexpected exception of type RemotelyClosedException was thrown") mustBe true
      }

    }
  }


}
