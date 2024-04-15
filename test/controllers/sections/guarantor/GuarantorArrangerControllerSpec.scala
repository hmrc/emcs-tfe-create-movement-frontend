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

package controllers.sections.guarantor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.guarantor.GuarantorArrangerFormProvider
import mocks.services.MockUserAnswersService
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.{CheckMode, NormalMode, UserAddress, UserAnswers, VatNumberModel}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor._
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.GuarantorArrangerView

import scala.concurrent.Future

class GuarantorArrangerControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val guarantorArrangerRoute: String = controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val guarantorArrangerRouteCheckMode: String = controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testErn, testDraftId, CheckMode).url

  lazy val formProvider: GuarantorArrangerFormProvider = new GuarantorArrangerFormProvider()
  lazy val form: Form[GuarantorArranger] = formProvider()
  lazy val view: GuarantorArrangerView = app.injector.instanceOf[GuarantorArrangerView]

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, guarantorArrangerRoute)

    lazy val testController = new GuarantorArrangerController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      messagesControllerComponents,
      view
    )

  }

  "GuarantorArranger Controller" - {
    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers.set(GuarantorRequiredPage, true))) {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, GuarantorArranger.values.head))) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(GuarantorArranger.values.head), NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers.set(GuarantorRequiredPage, true))) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers.set(GuarantorRequiredPage, true)))

      val req = FakeRequest(POST, guarantorArrangerRoute).withFormUrlEncodedBody(("value", GuarantorArranger.values.head.toString))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers.set(GuarantorRequiredPage, true))) {
      val req = FakeRequest(POST, guarantorArrangerRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to guarantor index controller for a GET if no guarantor required is found" in new Fixture() {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, guarantorArrangerRoute).withFormUrlEncodedBody(("value", GuarantorArranger.values.head.toString))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    Seq(Consignee, Consignor).foreach(
      guarantorArranger =>

        s"must cleanse further guarantor sections when choosing an arranger of ${guarantorArranger.getClass.getSimpleName.stripSuffix("$")}" in new Fixture(
          Some(emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, Transporter)
            .set(GuarantorNamePage, "Some name")
            .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("GB12345678")))
            .set(GuarantorAddressPage, UserAddress(Some("1"), "Street", "town", "AA11AA")))) {

          val expectedAnswers = emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, guarantorArranger)

          MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

          val req = FakeRequest(POST, guarantorArrangerRoute).withFormUrlEncodedBody(("value", guarantorArranger.toString))

          val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
    )

    Seq(GoodsOwner, Transporter).foreach {
      guarantorArranger =>
        Seq(Consignor, Consignee).foreach(
          consignorOrConsigneeOldAnswer =>
            s"must force NormalMode if new answer is ${guarantorArranger.getClass.getSimpleName.stripSuffix("$")}" +
              s" and old answer is ${consignorOrConsigneeOldAnswer.getClass.getSimpleName.stripSuffix("$")}" in new Fixture(
              Some(emptyUserAnswers
                .set(GuarantorRequiredPage, true)
                .set(GuarantorArrangerPage, consignorOrConsigneeOldAnswer))) {

              MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

              val req = FakeRequest(POST, guarantorArrangerRouteCheckMode).withFormUrlEncodedBody(("value", guarantorArranger.toString))

              val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
        )
        Seq(GoodsOwner, Transporter).foreach(
          oldAnswer =>
            s"must keep old Mode if new answer is ${guarantorArranger.getClass.getSimpleName.stripSuffix("$")}" +
              s" and old answer is ${oldAnswer.getClass.getSimpleName.stripSuffix("$")}" in new Fixture(
              Some(emptyUserAnswers
                .set(GuarantorRequiredPage, true)
                .set(GuarantorArrangerPage, oldAnswer))) {

              if (oldAnswer != guarantorArranger) {
                MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
              }

              val req = FakeRequest(POST, guarantorArrangerRouteCheckMode).withFormUrlEncodedBody(("value", guarantorArranger.toString))

              val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
        )
    }
  }
}
