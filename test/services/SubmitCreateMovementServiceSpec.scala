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
import featureswitch.core.config.EnableNRS
import fixtures.NRSBrokerFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockSubmitCreateMovementConnector
import mocks.services.{MockAuditingService, MockNRSBrokerService}
import models.audit.SubmitCreateMovementAudit
import models.response.UnexpectedDownstreamDraftSubmissionResponseError
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import java.time.{Instant, LocalDateTime}
import scala.concurrent.{ExecutionContext, Future}

class SubmitCreateMovementServiceSpec extends SpecBase
  with MockSubmitCreateMovementConnector
  with MockAuditingService
  with MockNRSBrokerService
  with MockAppConfig
  with NRSBrokerFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val timeMachine: TimeMachine = new TimeMachine {
    override def now(): LocalDateTime = LocalDateTime.parse(testReceiptDateTime)
    override def instant(): Instant = Instant.now()
  }

  lazy val testService = new SubmitCreateMovementService(mockSubmitCreateMovementConnector, mockNRSBrokerService, mockAuditingService, timeMachine, mockAppConfig)

  class Fixture(isNRSEnabled: Boolean) {

    MockAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)

    if (isNRSEnabled) {
      MockNRSBrokerService.submitPayload(minimumSubmitCreateMovementModel, testErn)
        .returns(Future.successful(Right(nrsBrokerResponseModel)))
    } else {
      MockNRSBrokerService.submitPayload(minimumSubmitCreateMovementModel, testErn).never()
    }
  }

  ".submit(ern: String, submission: SubmitCreateMovementModel)" - {

    Seq(true, false).foreach { nrsEnabled =>

      s"when NRS enabled is '$nrsEnabled'" - {

        "should return Success response" - {

          "when Connector returns success from downstream" in new Fixture(nrsEnabled) {

            val request = dataRequest(FakeRequest())

            MockSubmitCreateMovementConnector.submit(minimumSubmitCreateMovementModel).returns(Future.successful(Right(submitCreateMovementResponseEIS)))

            MockAuditingService
              .audit(SubmitCreateMovementAudit(testErn, testReceiptDateTime, minimumSubmitCreateMovementModel, Right(submitCreateMovementResponseEIS)))
              .once()

            testService.submit(minimumSubmitCreateMovementModel, testErn)(request, hc).futureValue mustBe Right(submitCreateMovementResponseEIS)
          }
        }

        "should return Failure response" - {

          "when Connector returns failure from downstream" in new Fixture(nrsEnabled) {

            val request = dataRequest(FakeRequest())

            MockSubmitCreateMovementConnector.submit(minimumSubmitCreateMovementModel).returns(Future.successful(Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))))

            MockAuditingService
              .audit(SubmitCreateMovementAudit(testErn, testReceiptDateTime, minimumSubmitCreateMovementModel, Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))))
              .once()

            testService.submit(minimumSubmitCreateMovementModel, testErn)(request, hc).futureValue mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))
          }
        }
      }
    }
  }
}
