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
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, UkTaxWarehouse}
import models.sections.journeyType.HowMovementTransported.AirTransport
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
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

  def guarantorRequiredRoute(ern: String): Call = routes.GuarantorRequiredController.onPageLoad(ern, testDraftId, NormalMode)

  def onSubmitRoute(ern: String): Call = routes.GuarantorRequiredController.onSubmit(ern, testDraftId, NormalMode)

  lazy val enterGuarantorDetailsRoute = (ern: String) => routes.GuarantorRequiredController.enterGuarantorDetails(ern, testDraftId)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers), ern: String = testGreatBritainWarehouseKeeperErn) {

    val request = FakeRequest(GET, guarantorRequiredRoute(ern).url)

    lazy val testController = new GuarantorRequiredController(
      messagesApi,
      mockUserAnswersService,
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

    "must return OK and the correct view for a GET (guarantor not always required UK to UK)" in new Fixture(
      Some(
        emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB)
      )) {

      val result = testController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, onSubmitRoute(testGreatBritainWarehouseKeeperErn))(dataRequest(request, ern = testGreatBritainWarehouseKeeperErn), messages(request)).toString
    }

    "must return OK and the correct view when the guarantor will always be required (GB to EU)" in new Fixture(
      Some(emptyUserAnswers
        .set(DestinationTypePage, EuTaxWarehouse)
      )) {

      val result = testController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, enterGuarantorDetailsRoute(testGreatBritainWarehouseKeeperErn), requiredGuarantee = true)(dataRequest(request, ern = testGreatBritainWarehouseKeeperErn), messages(request)).toString
    }

    "must return OK and the correct view when the guarantor will always be required for NI to EU" in new Fixture(
      Some(emptyUserAnswers
        .set(DestinationTypePage, EuTaxWarehouse)
        .set(HowMovementTransportedPage, AirTransport)
      ),
      testNorthernIrelandErn
    ) {

      val result = testController.onPageLoad(testNorthernIrelandErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, enterGuarantorDetailsRoute(testNorthernIrelandErn), requiredGuaranteeNIToEU = true)(dataRequest(request, ern = testNorthernIrelandErn), messages(request)).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers
        .set(DestinationTypePage, UkTaxWarehouse.GB)
        .set(GuarantorRequiredPage, true)
      )
    ) {

      val result = testController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), onSubmitRoute(testGreatBritainWarehouseKeeperErn))(dataRequest(request, ern = testGreatBritainWarehouseKeeperErn), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(
      Some(emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB))
    ) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute(testGreatBritainWarehouseKeeperErn).url).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
      Some(emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB))
    ) {
      val req = FakeRequest(POST, guarantorRequiredRoute(testGreatBritainWarehouseKeeperErn).url).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, onSubmitRoute(testGreatBritainWarehouseKeeperErn))(dataRequest(request, ern = testGreatBritainWarehouseKeeperErn), messages(request)).toString
    }

    "must redirect to the next page for enterGuarantorDetails route" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = testController.enterGuarantorDetails(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must wipe any previous answer for GuarantorRequired page" in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, UkTaxWarehouse.GB)
      .set(GuarantorRequiredPage, true)
      .set(GuarantorArrangerPage, Transporter)
    )) {

      val expectedAnswers = emptyUserAnswers
        .set(DestinationTypePage, UkTaxWarehouse.GB)
        .set(GuarantorArrangerPage, Transporter)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = testController.enterGuarantorDetails(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET onPageLoad if no existing data is found" in new Fixture(None) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST onSubmit if no existing data is found" in new Fixture(None) {

      val req = FakeRequest(POST, guarantorRequiredRoute(testErn).url).withFormUrlEncodedBody(("value", "true"))

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
        .set(DestinationTypePage, UkTaxWarehouse.GB)
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter))
    ) {

      val expectedAnswers = emptyUserAnswers
        .set(DestinationTypePage, UkTaxWarehouse.GB)
        .set(GuarantorRequiredPage, false)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute(testGreatBritainWarehouseKeeperErn).url).withFormUrlEncodedBody(("value", "false"))

      val result = testController.onSubmit(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }

    "must cleanse the guarantor section when answering yes" in new Fixture(
      Some(emptyUserAnswers
        .set(DestinationTypePage, UkTaxWarehouse.GB)
        .set(GuarantorRequiredPage, false)
        .set(GuarantorArrangerPage, Transporter))
    ) {

      val expectedAnswers = emptyUserAnswers
        .set(DestinationTypePage, UkTaxWarehouse.GB)
        .set(GuarantorRequiredPage, true)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute(testGreatBritainWarehouseKeeperErn).url).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }
  }
}
