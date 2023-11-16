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

package controllers.sections.consignee

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import forms.sections.consignee.ConsigneeBusinessNameFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeBusinessNamePage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignee.ConsigneeBusinessNameView

import scala.concurrent.Future

class ConsigneeBusinessNameControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val onwardRoute = Call("GET", "/foo")
    val formProvider = new ConsigneeBusinessNameFormProvider()
    val form = formProvider()
    val request = FakeRequest()
    val consigneeBusinessNameSubmit = controllers.sections.consignee.routes.ConsigneeBusinessNameController.onSubmit(testErn, testDraftId, NormalMode)
    lazy val view = app.injector.instanceOf[ConsigneeBusinessNameView]

    object TestController extends ConsigneeBusinessNameController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(onwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "ConsigneeBusinessName Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)


      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, consigneeBusinessNameSubmit)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ConsigneeBusinessNamePage, "answer"))) {

      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), consigneeBusinessNameSubmit)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest().withFormUrlEncodedBody(("value", "answer"))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest().withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, consigneeBusinessNameSubmit)(dataRequest(req), messages(req)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest().withFormUrlEncodedBody(("value", "answer"))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
