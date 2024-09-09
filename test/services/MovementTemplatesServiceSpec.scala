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
import fixtures.TemplateFixtures
import mocks.connectors.MockMovementTemplatesConnector
import models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.{ExecutionContext, Future}

class MovementTemplatesServiceSpec extends SpecBase
  with MockMovementTemplatesConnector
  with TemplateFixtures
  with LogCapturing {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new MovementTemplatesService(mockMovementTemplatesConnector)

  ".userHasTemplates" - {

    "should return 'true'" - {

      "when Connector returns seq of templates" in {
        MockMovementTemplatesConnector.getList(testErn).returns(Future(Right(Seq(templateModel))))
        val result = testService.userHasTemplates(testErn).futureValue
        result mustBe true
      }
    }

    "should return 'false'" - {

      "when Connector returns empty seq" in {
        MockMovementTemplatesConnector.getList(testErn).returns(Future(Right(Seq())))
        val result = testService.userHasTemplates(testErn).futureValue
        result mustBe false
      }

      "when Connector fails (but log a warning message)" in {
        withCaptureOfLoggingFrom(testService.logger) { logs =>
          MockMovementTemplatesConnector.getList(testErn).returns(Future(Left(UnexpectedDownstreamResponseError)))
          val result = testService.userHasTemplates(testErn).futureValue
          result mustBe false
          logs.exists(_.getMessage == "[MovementTemplatesService][userHasTemplates] Failed to retrieve templates from emcs-tfe, defaulting response to false") mustBe true
        }
      }
    }
  }
}
