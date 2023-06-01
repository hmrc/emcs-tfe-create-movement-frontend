/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors.addressLookupFrontend

import base.SpecBase
import config.AppConfig
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, MissingHeaderError, UnexpectedDownstreamResponseError}
import play.api.Application
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AddressLookupFrontendConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockHttpClient {

  lazy val app: Application = applicationBuilder().build()

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  lazy val connector: AddressLookupFrontendConnector = new AddressLookupFrontendConnector(mockHttpClient, appConfig)

  ".retrieveAddress()" - {
    "should return a successful response" - {
      "when downstream call is successful and returns some JSON" in {
        MockHttpClient.get(s"${appConfig.addressLookupFrontendUrl}/api/confirmed?id=$testId")
          .returns(Future.successful(Right(Some(testAlfAddress))))

        connector.retrieveAddress(testId).futureValue mustBe Right(Some(testAlfAddress))
      }

      "when downstream call is successful and returns None" in {
        MockHttpClient.get(s"${appConfig.addressLookupFrontendUrl}/api/confirmed?id=$testId")
          .returns(Future.successful(Right(None)))

        connector.retrieveAddress(testId).futureValue mustBe Right(None)
      }
    }

    "should return an error response" - {
      "when downstream call fails" in {
        MockHttpClient.get(s"${appConfig.addressLookupFrontendUrl}/api/confirmed?id=$testId")
          .returns(Future.successful(Left(JsonValidationError)))

        connector.retrieveAddress(testId).futureValue mustBe Left(JsonValidationError)
      }
    }

    "should return an error response" - {
      "when there is an unexpected error" in {
        MockHttpClient.get(s"${appConfig.addressLookupFrontendUrl}/api/confirmed?id=$testId")
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.retrieveAddress(testId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  ".initialiseJourney()" - {
    "should return a successful response" - {
      "when downstream call is successful and returns some JSON" in {
        MockHttpClient.post(
          url = s"${appConfig.addressLookupFrontendUrl}/api/init",
          body = testAlfJourneyConfig
        ).returns(Future.successful(Right(testUrl)))

        connector.initialiseJourney(testAlfJourneyConfig).futureValue mustBe Right(testUrl)
      }
    }

    "should return an error response" - {
      "when downstream call fails" in {
        MockHttpClient.post(
          url = s"${appConfig.addressLookupFrontendUrl}/api/init",
          body = testAlfJourneyConfig
        ).returns(Future.successful(Left(MissingHeaderError)))

        connector.initialiseJourney(testAlfJourneyConfig).futureValue mustBe Left(MissingHeaderError)
      }
    }

    "should return an error response" - {
      "when there is an unexpected error" in {
        MockHttpClient.post(
          url = s"${appConfig.addressLookupFrontendUrl}/api/init",
          body = testAlfJourneyConfig
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.initialiseJourney(testAlfJourneyConfig).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

}