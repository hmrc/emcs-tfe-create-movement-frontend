package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.{Fault, HttpHeader, HttpHeaders}
import connectors.addressLookupFrontend.AddressLookupFrontendConnector
import models.addressLookupFrontend._
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.EitherValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.HeaderNames
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier

class AddressLookupFrontendConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token",
        "microservice.services.address-lookup-frontend.port" -> server.port
      )
      .build()

  def retrieveAddressUrl(id: String) = s"/api/confirmed?id=$id"

  val initialiseJourneyUrl = s"/api/init"

  private lazy val connector: AddressLookupFrontendConnector = app.injector.instanceOf[AddressLookupFrontendConnector]

  val testAlfAddressJson: JsObject =
    Json.obj(
      "auditRef" -> "bed4bd24-72da-42a7-9338-f43431b7ed72",
      "address" -> Json.obj(
        "lines" -> Json.arr("10 Other Place", "Some District", "Anytown"),
        "postcode" -> "ZZ1 1ZZ",
        "country" -> Json.obj(
          "code" -> "GB",
          "name" -> "United Kingdom"
        )
      )
    )

  val testAlfAddress: Address =
    Address(
      lines = Seq("10 Other Place", "Some District", "Anytown"),
      postcode = Some("ZZ1 1ZZ"),
      country = Some(Country("GB", "United Kingdom")),
      auditRef = Some("bed4bd24-72da-42a7-9338-f43431b7ed72")
    )

  val testId = "123456"

  ".retrieveAddress" - {
    val response = aResponse().withBody(Json.stringify(testAlfAddressJson))
    "must return an address when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(retrieveAddressUrl(testId)))
          .willReturn(response.withStatus(OK))
      )

      connector.retrieveAddress(testId).futureValue mustBe Right(Some(testAlfAddress))
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(retrieveAddressUrl(testId)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.retrieveAddress(testId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(retrieveAddressUrl(testId)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.retrieveAddress(testId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

  ".initialiseJourney" - {
    val testAlfJourneyConfig: AddressLookupFrontendJourneyConfig =
      AddressLookupFrontendJourneyConfig(
        version = 2,
        options = JourneyOptions(
          continueUrl = "testContinueUrl",
          homeNavHref = None,
          accessibilityFooterUrl = None,
          deskProServiceName = None,
          showPhaseBanner = None,
          alphaPhase = None,
          showBackButtons = None,
          includeHMRCBranding = None,
          selectPageConfig = None,
          confirmPageConfig = None,
          timeoutConfig = None,
          disableTranslations = None
        ),
        labels = JourneyLabels(en = None, cy = None)
      )

    val body = Json.toJson(testAlfJourneyConfig)

    val callbackUrl = "testUrl"

    val header = new HttpHeaders(new HttpHeader(HeaderNames.LOCATION, callbackUrl))

    "must return call back url when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(initialiseJourneyUrl))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withStatus(ACCEPTED).withBody(Json.stringify(body)).withHeaders(header))
      )

      connector.initialiseJourney(testAlfJourneyConfig).futureValue mustBe Right(callbackUrl)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(initialiseJourneyUrl))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.initialiseJourney(testAlfJourneyConfig).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(initialiseJourneyUrl))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.initialiseJourney(testAlfJourneyConfig).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

}
