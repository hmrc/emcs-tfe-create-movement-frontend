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

package controllers.sections.transportArranger

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import forms.sections.transportArranger.TransportArrangerNameFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger.{Consignee, GoodsOwner, Other}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerNamePage, TransportArrangerPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportArranger.TransportArrangerNameView

import scala.concurrent.Future

class TransportArrangerNameControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val formProvider = new TransportArrangerNameFormProvider()
    val form = formProvider()

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    val view = app.injector.instanceOf[TransportArrangerNameView]

    lazy val controller = new TransportArrangerNameController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportArrangerName Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers
        .set(TransportArrangerPage, Other)
        .set(TransportArrangerNamePage, "answer")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), Other, NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(
      emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)
    )) {
      MockUserAnswersService.set().returns(Future.successful(userAnswers.get))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)
    )) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, GoodsOwner, NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to the transport arranger controller for a GET if no transport arranger value is found" in new Test() {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Journey Recovery for a GET if the transport arranger value is invalid for this controller/page" in new Test(Some(
      emptyUserAnswers.set(TransportArrangerPage, Consignee)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }


    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
