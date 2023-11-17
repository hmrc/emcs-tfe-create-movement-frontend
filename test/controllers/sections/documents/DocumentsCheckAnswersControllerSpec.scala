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

package controllers.sections.documents

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockDocumentsCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeDocumentsNavigator
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.helpers.CheckYourAnswersDocumentsHelper
import views.html.sections.documents.DocumentsCheckAnswersView

class DocumentsCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockDocumentsCheckAnswersHelper {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    implicit val request = dataRequest(FakeRequest(GET, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url))
    implicit val msgs = messages(request)
    val view = app.injector.instanceOf[DocumentsCheckAnswersView]
    val summaryList = app.injector.instanceOf[CheckYourAnswersDocumentsHelper].summaryList()(request, msgs)

    object TestController extends DocumentsCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeDocumentsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents,
      view,
      mockDocumentsCheckAnswersHelper
    )
  }

  "DocumentsCheckAnswers Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      MockDocumentsCheckAnswersHelper.summaryList().returns(summaryList)

      val result = TestController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(summaryList).toString
    }

    "must redirect to task list for POST" in new Fixture() {
      val req = dataRequest(FakeRequest(POST, routes.DocumentsCheckAnswersController.onSubmit(testErn, testDraftId).url))

      val result = TestController.onSubmit(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = TestController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None){
      val req = FakeRequest(POST, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url).withFormUrlEncodedBody(("value", "true"))

      val result = TestController.onSubmit(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
