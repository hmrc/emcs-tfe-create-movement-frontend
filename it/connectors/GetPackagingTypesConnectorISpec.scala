package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetPackagingTypesConnector
import fixtures.BaseFixtures
import models.response.UnexpectedDownstreamResponseError
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode
import models.sections.items.ItemBulkPackagingCode.{BulkGas, BulkLiquefiedGas, BulkLiquid}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetPackagingTypesConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = "/emcs-tfe-reference-data/oracle/packaging-types"

  val packagingCodes: Seq[ItemBulkPackagingCode] = Seq(BulkGas, BulkLiquefiedGas, BulkLiquid)

  ".getPackagingTypes" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "false")
        .build()

    lazy val connector: GetPackagingTypesConnector = app.injector.instanceOf[GetPackagingTypesConnector]

    s"must return Right(Seq[BulkPackagingType]) when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(bulkPackagingTypesJson.toString()))
      )

      connector.getPackagingTypes(packagingCodes).futureValue mustBe Right(bulkPackagingTypes)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getPackagingTypes(packagingCodes).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getPackagingTypes(packagingCodes).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
