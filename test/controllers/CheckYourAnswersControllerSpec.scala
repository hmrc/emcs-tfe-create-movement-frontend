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
import controllers.actions.{DataRequiredAction, FakeAuthAction, FakeDataRetrievalAction, FakeUserAllowListAction}
import handlers.ErrorHandler
import mocks.viewmodels.MockCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeNavigator
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency with MockCheckAnswersHelper {

  class Fixture(userAnswers: Option[UserAnswers]) {
    def onwardRoute: Call = Call("GET", "/foo")

    implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val messages: Messages = messagesApi.preferred(request)

    lazy val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
    val view: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

    val controller = new CheckYourAnswersController(
      messagesApi,
      app.injector.instanceOf[FakeAuthAction],
      app.injector.instanceOf[FakeUserAllowListAction],
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      Helpers.stubMessagesControllerComponents(),
      new FakeNavigator(testOnwardRoute),
      view,
      mockCheckAnswersHelper
    )
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

        val list = SummaryListViewModel(Seq.empty)

        MockCheckAnswersHelper.summaryList(Seq()).returns(list)

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe
          view(routes.CheckYourAnswersController.onSubmit(testErn, testDraftId), list)(dataRequest(request), messages).toString
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    ".onSubmit" - {

      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {

        val result = controller.onSubmit(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe testOnwardRoute.url
      }
    }
  }
}
