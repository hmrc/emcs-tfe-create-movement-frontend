package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetCommodityCodesConnector
import fixtures.BaseFixtures
import models.UnitOfMeasure.Kilograms
import models.response.UnexpectedDownstreamResponseError
import models.response.referenceData.CnCodeInformation
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetCommodityCodesConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url(exciseproductCode: String) = s"/emcs-tfe-reference-data/oracle/cn-codes/$exciseproductCode"

  val commodityCodes: Seq[CnCodeInformation] = Seq(
    CnCodeInformation(
      cnCode = "T400",
      cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
      exciseProductCode = testEpc,
      exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
      unitOfMeasure = Kilograms
    ),
    CnCodeInformation(
      cnCode = "T401",
      cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
      exciseProductCode = testEpc,
      exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
      unitOfMeasure = Kilograms
    )
  )

  lazy val testEpc: String = "24029000"

  ".getDocumentTypes" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "false")
        .build()

    lazy val connector: GetCommodityCodesConnector = app.injector.instanceOf[GetCommodityCodesConnector]

    "must return the commodity codes when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url(testEpc)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.arr(
                Json.obj(
                  "cnCode" -> "T400",
                  "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
                  "exciseProductCode" -> testEpc,
                  "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
                  "unitOfMeasureCode" -> 1
                ),
                Json.obj(
                  "cnCode" -> "T401",
                  "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
                  "exciseProductCode" -> testEpc,
                  "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
                  "unitOfMeasureCode" -> 1
                ),
              )))
          )
      )

      connector.getCommodityCodes(testEpc).futureValue mustBe Right(commodityCodes)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url(testEpc)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getCommodityCodes(testEpc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url(testEpc)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getCommodityCodes(testEpc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
