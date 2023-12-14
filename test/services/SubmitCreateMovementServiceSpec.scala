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

package services

import base.SpecBase
import mocks.connectors.MockSubmitCreateMovementConnector
import models.response.UnexpectedDownstreamResponseError
import models.response.SubmitCreateMovementException
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SubmitCreateMovementServiceSpec extends SpecBase with MockSubmitCreateMovementConnector {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new SubmitCreateMovementService(mockSubmitCreateMovementConnector)

  ".submit(ern: String, submission: SubmitCreateMovementModel)" - {

    "should return Success response" - {

      "when Connector returns success from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitCreateMovementConnector.submit(minimumSubmitCreateMovementModel).returns(Future.successful(Right(minimumSubmitCreateMovementResponse)))

        testService.submit(minimumSubmitCreateMovementModel)(request, hc).futureValue mustBe minimumSubmitCreateMovementResponse
      }
    }

    "should return Failure response" - {

      "when Connector returns failure from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitCreateMovementConnector.submit(minimumSubmitCreateMovementModel).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[SubmitCreateMovementException](await(testService.submit(minimumSubmitCreateMovementModel)(request, hc))).getMessage mustBe
          s"Failed to submit Create Movement to emcs-tfe for ern: '$testErn' & draftId: '$testDraftId'"
      }
    }
  }
}
