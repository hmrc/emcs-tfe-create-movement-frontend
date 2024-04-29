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

package controllers

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.MovementSubmissionFailureFixtures
import mocks.services.MockValidationService
import models.sections.info.movementScenario.MovementScenario
import pages.sections.info.DestinationTypePage
import play.api.Play.materializer
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DraftMovementView

import scala.concurrent.Future

class DraftMovementControllerSpec extends SpecBase with MockValidationService with MovementSubmissionFailureFixtures {

  "onPageLoad" - {

    val userAnswers = emptyUserAnswers.set(DestinationTypePage, MovementScenario.GbTaxWarehouse)
    lazy val view = app.injector.instanceOf[DraftMovementView]

    lazy val testController = new DraftMovementController(
      messagesApi,
      fakeAuthAction,
      fakeBetaAllowListAction,
      new FakeDataRetrievalAction(Some(userAnswers), Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents,
      mockValidationService,
      view
    )

    lazy val request = FakeRequest(GET, routes.DraftMovementController.onPageLoad(testErn, testDraftId).url)

    "when no validation errors are detected" - {

      "must render the page" in {

        MockValidationService.validate().returns(Future.successful(userAnswers))

        lazy val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(dataRequest(request, userAnswers), messages(request)).toString
      }
    }

    "when validation errors are detected" - {

      "must render the page including the generated submission failure for any submission failures" in {

        val answersWithErrors = userAnswers.copy(submissionFailures = Seq(dispatchDateInPastValidationError()))

        MockValidationService.validate().returns(Future.successful(answersWithErrors))

        lazy val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(dataRequest(request, answersWithErrors), messages(request)).toString
      }
    }
  }
}
