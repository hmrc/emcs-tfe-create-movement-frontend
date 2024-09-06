package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.MovementTemplatesConnector
import fixtures.{BaseFixtures, TemplateFixtures}
import models.requests.{DataRequest, UserRequest}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.ExecutionContext.Implicits.global

class MovementTemplatesConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures
  with LogCapturing
  with TemplateFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "features.stub-get-trader-known-facts" -> "false"
      )
      .build()

  lazy val connector: MovementTemplatesConnector = app.injector.instanceOf[MovementTemplatesConnector]

  implicit lazy val dr: DataRequest[_] =
    DataRequest(UserRequest(FakeRequest(), testErn, "", "", "", hasMultipleErns = false), testDraftId, emptyUserAnswers, Some(testMinTraderKnownFacts))

  ".getList" - {

    "when templates are returned from emcs-tfe" - {

      "must return the list of templates" in {

        server.stubFor(
          get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.stringify(Json.arr(templateJson)))
            )
        )

        connector.getList().futureValue mustBe Right(Seq(templateModel))
      }
    }

    "when no templates are returned from emcs-tfe" - {

      "must empty list" in {

        server.stubFor(
          get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
            .willReturn(
              aResponse()
                .withStatus(NO_CONTENT)
            )
        )

        connector.getList().futureValue mustBe Right(Seq())
      }
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getList().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      withCaptureOfLoggingFrom(connector.logger) { logs =>
        connector.getList().futureValue mustBe Left(UnexpectedDownstreamResponseError)
        logs.exists(_.getMessage == "[MovementTemplatesConnector][getList] Unexpected exception of type RemotelyClosedException was thrown") mustBe true
      }
    }
  }
}
