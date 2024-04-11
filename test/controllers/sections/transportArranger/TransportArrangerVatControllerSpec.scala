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
import forms.sections.transportArranger.TransportArrangerVatFormProvider
import forms.sections.transportArranger.TransportArrangerVatFormProvider.{hasVatNumberField, transportArrangerVatNumberField, transportArrangerVatNumberRequired}
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger.GoodsOwner
import models.sections.transportArranger.TransportArrangerVatModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportArranger.TransportArrangerVatView

import scala.concurrent.Future

class TransportArrangerVatControllerSpec extends SpecBase with MockUserAnswersService {

  val goodsOwnerUserAnswers: UserAnswers = emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)

  val inputModelWithVATNumber: TransportArrangerVatModel = TransportArrangerVatModel(hasTransportArrangerVatNumber = true, Some("GB123456789"))

  lazy val formProvider: TransportArrangerVatFormProvider = new TransportArrangerVatFormProvider()
  lazy val form: Form[TransportArrangerVatModel] = formProvider(GoodsOwner)
  lazy val view: TransportArrangerVatView = app.injector.instanceOf[TransportArrangerVatView]

  lazy val transportArrangerVatSubmitAction: Call = routes.TransportArrangerVatController.onSubmit(testErn, testDraftId, NormalMode)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(goodsOwnerUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportArrangerVatController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportArrangerVat Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, transportArrangerVatSubmitAction, GoodsOwner)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(goodsOwnerUserAnswers.set(TransportArrangerVatPage, inputModelWithVATNumber))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(inputModelWithVATNumber), transportArrangerVatSubmitAction, GoodsOwner)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted (no selected)" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(goodsOwnerUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody((hasVatNumberField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted (yes selected with VAT number)" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(goodsOwnerUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(hasVatNumberField -> "true", transportArrangerVatNumberField -> testVatNumber))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted (empty VAT number)" in new Fixture() {

      implicit val msgs: Messages = messages(request)

      val boundForm = form.fill(inputModelWithVATNumber.copy(transportArrangerVatNumber = None)).withError(transportArrangerVatNumberField, msgs(transportArrangerVatNumberRequired))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(hasVatNumberField -> "true", transportArrangerVatNumberField -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, transportArrangerVatSubmitAction, GoodsOwner)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
