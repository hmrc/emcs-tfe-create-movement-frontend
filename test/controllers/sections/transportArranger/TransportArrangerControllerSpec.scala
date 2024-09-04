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
import forms.sections.transportArranger.TransportArrangerFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import models.{NormalMode, UserAnswers, VatNumberModel}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage, TransportArrangerVatPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportArranger.TransportArrangerView

import scala.concurrent.Future

class TransportArrangerControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportArrangerFormProvider = new TransportArrangerFormProvider()
  lazy val form: Form[TransportArranger] = formProvider()(dataRequest(FakeRequest()))
  lazy val view: TransportArrangerView = app.injector.instanceOf[TransportArrangerView]

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller: TransportArrangerController = new TransportArrangerController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }


  "TransportArranger Controller" - {

    "must return OK and the correct view for a GET" in new Test() {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(TransportArrangerPage, TransportArranger.values.head)
    )) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(TransportArranger.values.head), NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", TransportArranger.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {

      val boundForm: Form[TransportArranger] = form.bind(Map("value" -> ""))

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", TransportArranger.values.head.toString)))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    Seq(Consignor, Consignee).foreach { arranger =>
      s"must cleanse the transport arranger data when a ${simpleName(arranger)} is selected" in new Test(
        Some(
          emptyUserAnswers
            .set(TransportArrangerPage, Other)
            .set(TransportArrangerAddressPage, testUserAddress)
            .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
        )
      ) {

        val expectedUserAnswers: UserAnswers = emptyUserAnswers
          .set(TransportArrangerPage, arranger)

        MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

        val result: Future[Result] = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", arranger.toString)))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    Seq(GoodsOwner -> Other, Other -> GoodsOwner).foreach { case (arrangerFrom, arrangerTo) =>
      s"must not cleanse the transport arranger data when toggling between ${simpleName(arrangerFrom)} and ${simpleName(arrangerTo)}" in new Test(
        Some(
          emptyUserAnswers
            .set(TransportArrangerPage, arrangerFrom)
            .set(TransportArrangerAddressPage, testUserAddress)
            .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
        )
      ) {

        val expectedUserAnswers: UserAnswers = emptyUserAnswers
          .set(TransportArrangerPage, arrangerTo)
          .set(TransportArrangerAddressPage, testUserAddress)
          .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some(testVatNumber)))

        MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

        val result: Future[Result] = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", arrangerTo.toString)))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

  }
}
