/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, delete, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.DeleteDraftMovementConnector
import fixtures.BaseFixtures
import models.requests.{DataRequest, UserRequest}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteDraftMovementConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures
  with LogCapturing {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = s"/emcs-tfe/user-answers/create-movement/$testErn/$testDraftId"

  def app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "features.stub-get-trader-known-facts" -> "false"
      )
      .build()

  lazy val connector: DeleteDraftMovementConnector = app.injector.instanceOf[DeleteDraftMovementConnector]

  implicit lazy val dr: DataRequest[_] =
    DataRequest(UserRequest(FakeRequest(), testErn, "", "", "", hasMultipleErns = false), testDraftId, emptyUserAnswers, Some(testMinTraderKnownFacts))

  ".deleteDraft" - {

    "must return true when the server responds with NO_CONTENT" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(NO_CONTENT)
          )
      )

      connector.deleteDraft().futureValue mustBe Right(true)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.deleteDraft().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      withCaptureOfLoggingFrom(connector.logger) { logs =>

        connector.deleteDraft().futureValue mustBe Left(UnexpectedDownstreamResponseError)

        logs.exists(_.getMessage == "[DeleteDraftMovementConnector][delete] Unexpected exception of type RemotelyClosedException was thrown") mustBe true
      }
    }
  }
}
