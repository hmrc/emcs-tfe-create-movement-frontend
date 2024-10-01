package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, get, put, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.MovementTemplatesConnector
import fixtures.{BaseFixtures, ItemFixtures, TemplateFixtures}
import models.response.UnexpectedDownstreamResponseError
import models.response.templates.MovementTemplates
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.{Application, inject}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing
import utils.{TimeMachine, UUIDGenerator}

import java.time.{Instant, LocalDateTime}
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
  with ItemFixtures
  with TemplateFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val _now = LocalDateTime.now()
  val _instant = Instant.now()

  implicit val mockUUIDGenerator: UUIDGenerator = new UUIDGenerator {
    override def randomUUID(): String = templateId
  }

  implicit val timeMachine: TimeMachine = new TimeMachine {
    override def now(): LocalDateTime = _now
    override def instant(): Instant = _instant
  }

  def app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "features.stub-get-trader-known-facts" -> "false"
      )
      .overrides(inject.bind(classOf[UUIDGenerator]).toInstance(mockUUIDGenerator))
      .build()

  lazy val connector: MovementTemplatesConnector = app.injector.instanceOf[MovementTemplatesConnector]

  ".getList" - {

    "when templates are returned from emcs-tfe" - {

      "must return the list of templates" in {

        server.stubFor(
          get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.stringify(Json.obj("templates" -> Json.arr(templateJson), "count" -> 1)))
            )
        )

        connector.getList(testErn).futureValue mustBe Right(MovementTemplates(Seq(templateModel), 1))
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

        connector.getList(testErn).futureValue mustBe Right(MovementTemplates.empty)
      }
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getList(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(s"/emcs-tfe/templates/$testErn"))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      withCaptureOfLoggingFrom(connector.logger) { logs =>
        connector.getList(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
        logs.exists(_.getMessage == "[MovementTemplatesConnector][getList] Unexpected exception of type RemotelyClosedException was thrown") mustBe true
      }
    }
  }

  ".saveTemplate" - {

    "when success response is received" - {

      "must return true" in {

        val template = baseFullUserAnswers.toTemplate(templateId, None)

        server.stubFor(
          put(urlEqualTo(s"/emcs-tfe/template/${template.ern}/${template.templateId}"))
            .withRequestBody(equalToJson(Json.toJson(template).toString()))
            .willReturn(aResponse().withStatus(OK))
        )

        connector.saveTemplate(template).futureValue mustBe Right(true)
      }
    }

    "must fail when the server responds with any other status" in {

      val template = baseFullUserAnswers.toTemplate(templateId, None)

      server.stubFor(
        put(urlEqualTo(s"/emcs-tfe/template/${template.ern}/${template.templateId}"))
          .withRequestBody(equalToJson(Json.toJson(template).toString()))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.saveTemplate(template).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      val template = baseFullUserAnswers.toTemplate(templateId, None)

      server.stubFor(
        put(urlEqualTo(s"/emcs-tfe/template/${template.ern}/${template.templateId}"))
          .withRequestBody(equalToJson(Json.toJson(template).toString()))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      withCaptureOfLoggingFrom(connector.logger) { logs =>
        connector.saveTemplate(template).futureValue mustBe Left(UnexpectedDownstreamResponseError)
        logs.exists(_.getMessage == "[MovementTemplatesConnector][saveTemplate] Unexpected exception of type RemotelyClosedException was thrown") mustBe true
      }
    }
  }
}
