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

package controllers.sections.consignor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.consignor.ConsignorPaidTemporaryAuthorisationCodeFormProvider
import mocks.services.MockUserAnswersService
import models.requests.DataRequest
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsignorNavigator
import pages.sections.consignor.ConsignorPaidTemporaryAuthorisationCodePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.consignor.ConsignorPaidTemporaryAuthorisationCodeView

import scala.concurrent.Future

class ConsignorPaidTemporaryAuthorisationCodeControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider = new ConsignorPaidTemporaryAuthorisationCodeFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeView]

  class Test(val optUserAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    implicit val dr: DataRequest[_] = optUserAnswers match {
      case Some(answers) => dataRequest(request, answers, answers.ern)
      case None => dataRequest(request, emptyUserAnswers, testErn)
    }

    lazy val controller = new ConsignorPaidTemporaryAuthorisationCodeController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsignorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ConsignorPaidTemporaryAuthorisationCode Controller" - {
    "when logged in as a NorthernIrelandCertifiedConsignor user" - {
      val onSubmit = routes.ConsignorPaidTemporaryAuthorisationCodeController.onSubmit(testNICertifiedConsignorErn, testDraftId, NormalMode)
      val userAnswers = emptyUserAnswers.copy(ern = testNICertifiedConsignorErn)

      "must return OK and the correct view for a GET" in new Test(Some(userAnswers)) {
        val result = controller.onPageLoad(testNICertifiedConsignorErn, testDraftId, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, onSubmit)(dr, messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in
        new Test(Some(userAnswers.set(ConsignorPaidTemporaryAuthorisationCodePage, "answer"))) {
          val result = controller.onPageLoad(testNICertifiedConsignorErn, testDraftId, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form.fill("answer"), onSubmit)(dr, messages(request)).toString
        }

      "must redirect to the next page when valid data is submitted" in new Test(Some(userAnswers)) {
        MockUserAnswersService.set().returns(Future.successful(userAnswers))

        val result = controller.onSubmit(testNICertifiedConsignorErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "XIPTA12345678")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when an incorrect formatted PTA code is submitted" in new Test(Some(userAnswers)) {
        val boundForm = form.bind(Map("value" -> testGreatBritainErn))

        val result = controller.onSubmit(testNICertifiedConsignorErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", testGreatBritainErn)))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, onSubmit)(dr, messages(request)).toString
      }

      "must return a Bad Request and errors when a blank value is submitted" in new Test(Some(userAnswers)) {
        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onSubmit(testNICertifiedConsignorErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, onSubmit)(dr, messages(request)).toString
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
        val result = controller.onPageLoad(testNICertifiedConsignorErn, testDraftId, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
        val result = controller.onSubmit(testNICertifiedConsignorErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "when NOT logged in as a NorthernIrelandCertifiedConsignor user" - {
      val userAnswers = emptyUserAnswers.copy(ern = testNITemporaryCertifiedConsignorErn)

      "for a GET request" - {
        "must direct to ConsignorAddressController" in new Test(Some(userAnswers)) {
          val result = controller.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId, NormalMode)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual
            controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId, NormalMode).url
        }
      }

      "for a POST request" - {
        "must direct to ConsignorAddressController" in new Test(Some(userAnswers)) {
          val result = controller.onSubmit(testNITemporaryCertifiedConsignorErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual
            controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId, NormalMode).url
        }
      }
    }
  }
}
