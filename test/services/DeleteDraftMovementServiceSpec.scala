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

package services

import base.SpecBase
import mocks.connectors.MockDeleteDraftMovementConnector
import models.requests.DataRequest
import models.response.{DeleteDraftMovementException, UnexpectedDownstreamResponseError}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DeleteDraftMovementServiceSpec extends SpecBase with MockDeleteDraftMovementConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new DeleteDraftMovementService(mockDeleteDraftMovementConnector)

  implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  ".deleteDraft" - {

    "should return true" - {

      "when Connector returns success from downstream" in {

        MockDeleteDraftMovementConnector.deleteDraft().returns(Future(Right(true)))

        val result = testService.deleteDraft().futureValue

        result mustBe true
      }
    }

    "should throw DeleteDraftMovementException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "Failed to delete the users draft movement"

        MockDeleteDraftMovementConnector.deleteDraft().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[DeleteDraftMovementException](await(testService.deleteDraft())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
