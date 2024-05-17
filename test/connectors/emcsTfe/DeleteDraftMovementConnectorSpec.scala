/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors.emcsTfe

import base.SpecBase
import mocks.connectors.MockHttpClient
import models.requests.DataRequest
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DeleteDraftMovementConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val dr: DataRequest[_] = dataRequest(FakeRequest())

  lazy val connector = new DeleteDraftMovementConnector(mockHttpClient, appConfig)

  ".deleteDraft" - {

    "should return a successful response" - {

      "when downstream call is successful" in {

        MockHttpClient.delete(
          url = s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/$testErn/$testDraftId"
        ).returns(Future.successful(Right(true)))

        connector.deleteDraft().futureValue mustBe Right(true)
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        MockHttpClient.delete(
          url = s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/$testErn/$testDraftId"
        ).returns(Future.successful(Left(JsonValidationError)))

        connector.deleteDraft().futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
