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
import forms.sections.guarantor.GuarantorRequiredFormProvider
import mocks.services.MockUserAnswersService
import models.sections.guarantor.GuarantorArranger.Transporter
import models.sections.info.movementScenario.MovementScenario.EuTaxWarehouse
import models.sections.journeyType.HowMovementTransported.AirTransport
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.GuarantorRequiredView

import scala.concurrent.Future

class GuarantorRequiredControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: GuarantorRequiredFormProvider = new GuarantorRequiredFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: GuarantorRequiredView = app.injector.instanceOf[GuarantorRequiredView]

  lazy val guarantorRequiredRoute: Call = routes.GuarantorRequiredController.onPageLoad(testErn, testDraftId, NormalMode)
  lazy val onSubmitRoute: Call = routes.GuarantorRequiredController.onSubmit(testErn, testDraftId, NormalMode)
  lazy val enterGuarantorDetailsRoute = (ern: String) => routes.GuarantorRequiredController.enterGuarantorDetails(ern, testDraftId)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, guarantorRequiredRoute.url)

    lazy val testController = new GuarantorRequiredController(
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

  "GuarantorRequired Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, onSubmitRoute)(dataRequest(request), messages(request)).toString
    }

    "must return OK and the correct view when the guarantor will always be required" in new Fixture(Some(emptyUserAnswers
      .set(ConsigneeExcisePage, "AARC123456789")
    )) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, enterGuarantorDetailsRoute(testErn), requiredGuarantee = true)(dataRequest(request, ern = testErn), messages(request)).toString
    }

    "must return OK and the correct view when the guarantor will always be required for NI to EU" in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, EuTaxWarehouse)
      .set(HowMovementTransportedPage, AirTransport)
    )) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, enterGuarantorDetailsRoute(testErn), requiredGuaranteeNIToEU = true)(dataRequest(request), messages(request)).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(GuarantorRequiredPage, true))) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), onSubmitRoute)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute.url).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, guarantorRequiredRoute.url).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, onSubmitRoute)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page for enterGuarantorDetails route" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = testController.enterGuarantorDetails(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must wipe any previous answer for GuarantorRequired page" in new Fixture(Some(emptyUserAnswers
      .set(GuarantorRequiredPage, true)
      .set(GuarantorArrangerPage, Transporter)
      .set(GuarantorNamePage, "a name")
    )) {

      val expectedAnswers = emptyUserAnswers
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorNamePage, "a name")

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = testController.enterGuarantorDetails(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET onPageLoad if no existing data is found" in new Fixture(None) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST onSubmit if no existing data is found" in new Fixture(None) {

      val req = FakeRequest(POST, guarantorRequiredRoute.url).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a GET enterGuarantorDetails if no existing data is found" in new Fixture(None) {

      val result = testController.enterGuarantorDetails(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must cleanse the guarantor section when answering no" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorNamePage, "a name"))) {

      val expectedAnswers = emptyUserAnswers.set(GuarantorRequiredPage, false)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute.url).withFormUrlEncodedBody(("value", "false"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }

    "must cleanse the guarantor section when answering yes" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, false)
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorNamePage, "a name"))) {

      val expectedAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute.url).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }
  }
}
