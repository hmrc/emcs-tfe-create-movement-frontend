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
import mocks.services.MockAuditingService
import models.audit.SubmitCreateMovementAudit
import models.response.UnexpectedDownstreamDraftSubmissionResponseError
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class SubmitCreateMovementServiceSpec extends SpecBase with MockSubmitCreateMovementConnector with MockAuditingService {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val timeMachine: TimeMachine = () => LocalDateTime.parse(testReceiptDateTime)

  lazy val testService = new SubmitCreateMovementService(mockSubmitCreateMovementConnector, mockAuditingService, timeMachine)

  ".submit(ern: String, submission: SubmitCreateMovementModel)" - {

    "should return Success response" - {

      "when Connector returns success from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitCreateMovementConnector.submit(minimumSubmitCreateMovementModel).returns(Future.successful(Right(submitCreateMovementResponseEIS)))

        MockAuditingService
          .audit(SubmitCreateMovementAudit(testErn, testReceiptDateTime, minimumSubmitCreateMovementModel, Right(submitCreateMovementResponseEIS)))
          .once()

        testService.submit(minimumSubmitCreateMovementModel)(request, hc).futureValue mustBe Right(submitCreateMovementResponseEIS)
      }
    }

    "should return Failure response" - {

      "when Connector returns failure from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitCreateMovementConnector.submit(minimumSubmitCreateMovementModel).returns(Future.successful(Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))))

        MockAuditingService
          .audit(SubmitCreateMovementAudit(testErn, testReceiptDateTime, minimumSubmitCreateMovementModel, Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))))
          .once()

        testService.submit(minimumSubmitCreateMovementModel)(request, hc).futureValue mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))
      }
    }
  }
}
