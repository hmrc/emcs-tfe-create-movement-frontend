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

package controllers.actions

import base.SpecBase
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import models.requests.UserRequest
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class BetaAllowListActionSpec extends SpecBase with MockFactory with MockBetaAllowListConnector with MockAppConfig {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val betaAllowListAction = new BetaAllowListActionImpl(
    betaAllowListConnector = mockBetaAllowListConnector,
    errorHandler = errorHandler,
    config = mockAppConfig
  )

  class Harness(enabled: Boolean, connectorResponse: Either[ErrorResponse, Boolean]) {

    if(enabled) {
      MockAppConfig.betaAllowListCheckingEnabled.returns(true)
      MockBetaAllowListConnector.check(testErn)
        .returns(Future.successful(connectorResponse))
    } else {
      MockAppConfig.betaAllowListCheckingEnabled.returns(false)
    }

    val result: Future[Result] = betaAllowListAction.invokeBlock(userRequest(FakeRequest()), { _: UserRequest[_] =>
      Future.successful(Ok)
    })
  }

  "BetaAllowListAction" - {

    "when the beta allow list checking feature is enabled" - {

      "when the connector returns true (on the list)" - {

        "must execute the supplied block" in new Harness(enabled = true, connectorResponse = Right(true)) {
          status(result) mustBe OK
        }
      }

      "when the connector returns false (NOT on the list)" - {

        "must return SEE_OTHER and redirect to the not on beta list page" in new Harness(enabled = true, connectorResponse = Right(false)) {
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.notOnPrivateBeta().url)
        }
      }

      "when the connector returns a Left" - {
        "render ISE" in new Harness(enabled = true, connectorResponse = Left(UnexpectedDownstreamResponseError)) {
          status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "when the beta allow list checking feature is disabled" - {

      "must execute the supplied block" in new Harness(enabled = false, connectorResponse = Right(false)) {
        status(result) mustBe OK
      }
    }
  }
}