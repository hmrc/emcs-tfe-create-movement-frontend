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
import handlers.ErrorHandler
import mocks.viewmodels.MockCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import viewmodels.govuk.SummaryListFluency
import viewmodels.helpers.CheckAnswersHelper
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency with MockCheckAnswersHelper {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application: Application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper),
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute))
        )
        .build()

    lazy val errorHandler: ErrorHandler = application.injector.instanceOf[ErrorHandler]
    val view: CheckYourAnswersView = application.injector.instanceOf[CheckYourAnswersView]
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId).url)

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val list = SummaryListViewModel(Seq.empty)

          MockCheckAnswersHelper.summaryList(Seq()).returns(list)

          val result = route(application, request).value

          status(result) mustBe OK
          contentAsString(result) mustBe
            view(routes.CheckYourAnswersController.onSubmit(testErn, testDraftId), list)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    ".onSubmit" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(testErn, testDraftId).url)

      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe testOnwardRoute.url
        }
      }
    }
  }
}
