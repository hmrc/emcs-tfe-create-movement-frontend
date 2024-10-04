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
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.http.Status.SEE_OTHER
import play.api.mvc.{ActionRefiner, Result, Results}
import play.api.test.FakeRequest

import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionSpec extends SpecBase
  with Results
  with MockAppConfig {

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val action: ActionRefiner[OptionalDataRequest, DataRequest] =
    new DataRequiredActionImpl(
      mockAppConfig
    )

  val successResponse = Future.successful(Ok("success!"))
  val block: DataRequest[_] => Future[Result] = _ => successResponse

  "refine" - {
    "must return a Right" - {
      "when the user answers are defined and not submitted" in {
        val request = OptionalDataRequest(
          userRequest(FakeRequest()),
          testDraftId,
          Some(emptyUserAnswers),
          Some(testMinTraderKnownFacts)
        )

        val result = action.invokeBlock(request, block).futureValue

        result mustBe successResponse.futureValue
      }
      "when the user answers are submitted and user is on the Confirmation screen" in {
        val request = OptionalDataRequest(
          userRequest(FakeRequest(controllers.routes.ConfirmationController.onPageLoad(testErn, testDraftId))),
          testDraftId,
          Some(emptyUserAnswers.copy(hasBeenSubmitted = true)),
          Some(testMinTraderKnownFacts)
        )

        val result = action.invokeBlock(request, block).futureValue

        result mustBe successResponse.futureValue
      }
    }

    "must return a Left" - {
      "when the user answers are None" in {
        val request = OptionalDataRequest(
          userRequest(FakeRequest()),
          testDraftId,
          None,
          Some(testMinTraderKnownFacts)
        )

        val result: Result = action.invokeBlock(request, block).futureValue

        result.header.status mustBe SEE_OTHER
        result.header.headers("Location") mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url
      }

      "when the user answers are submitted and the user is not on the Confirmation screen" in {
        MockAppConfig.emcsTfeFrontendHomeUrl.returns(testUrl)

        val request = OptionalDataRequest(
          userRequest(FakeRequest(controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId))),
          testDraftId,
          Some(emptyUserAnswers.copy(hasBeenSubmitted = true)),
          Some(testMinTraderKnownFacts)
        )

        val result: Result = action.invokeBlock(request, block).futureValue

        result.header.status mustBe SEE_OTHER
        result.header.headers("Location") mustBe testUrl
      }
    }
  }

}
