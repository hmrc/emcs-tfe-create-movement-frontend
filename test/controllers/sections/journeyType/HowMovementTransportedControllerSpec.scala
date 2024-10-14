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

package controllers.sections.journeyType

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import forms.sections.journeyType.HowMovementTransportedFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{UkTaxWarehouse, UnknownDestination}
import models.sections.journeyType.HowMovementTransported
import models.sections.transportUnit.TransportUnitType
import models.sections.transportUnit.TransportUnitType.{Container, FixedTransport, Tractor}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeJourneyTypeNavigator
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeDaysPage}
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitTypePage, TransportUnitsSection}
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.journeyType.{HowMovementTransportedNoOptionView, HowMovementTransportedView}

import scala.concurrent.Future

class HowMovementTransportedControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: HowMovementTransportedFormProvider = new HowMovementTransportedFormProvider()
  lazy val form: Form[HowMovementTransported] = formProvider()
  lazy val view: HowMovementTransportedView = app.injector.instanceOf[HowMovementTransportedView]
  lazy val onlyFixedView: HowMovementTransportedNoOptionView = app.injector.instanceOf[HowMovementTransportedNoOptionView]

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new HowMovementTransportedController(
      messagesApi,
      mockUserAnswersService,
      new FakeJourneyTypeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      onlyFixedView
    )
  }

  "HowMovementTransported Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, UkTaxWarehouse.GB)(dataRequest(request, baseUserAnswers), messages(request)).toString
    }

    "must return OK and the correct view for a GET (where the destination type is UnknownDestination)" in new Test(Some(
      emptyUserAnswers.set(DestinationTypePage, UnknownDestination)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, UnknownDestination)(dataRequest(request,
        emptyUserAnswers.set(DestinationTypePage, UnknownDestination)
      ), messages(request)).toString
    }
    
    Seq(
      MovementScenario.EuTaxWarehouse,
      MovementScenario.TemporaryRegisteredConsignee,
      MovementScenario.RegisteredConsignee,
      MovementScenario.DirectDelivery,
      MovementScenario.ExemptedOrganisation
    ).foreach { scenario =>
      s"must return OK and the onlyFixedView for guarantorNotRequired and destination type is $scenario" in new Test(Some(
        emptyUserAnswers.copy(ern = testNorthernIrelandErn).set(GuarantorRequiredPage, false).set(DestinationTypePage, scenario)
      )) {
        val result = controller.onPageLoad(testNorthernIrelandErn, testDraftId, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual onlyFixedView(NormalMode)(dataRequest(request, ern = testNorthernIrelandErn,
          answers = emptyUserAnswers.copy(ern = testNorthernIrelandErn).set(GuarantorRequiredPage, false).set(DestinationTypePage, scenario)
        ), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(HowMovementTransportedPage, HowMovementTransported.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(HowMovementTransported.values.head), NormalMode, UkTaxWarehouse.GB
      )(dataRequest(request, baseUserAnswers), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(baseUserAnswers)) {

      val expectedUserAnswers: UserAnswers = baseUserAnswers.set(HowMovementTransportedPage, HowMovementTransported.values.head)

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page setting the answer to fixed transport when guarantorNotRequired and movement is uk to eu" in new Test(Some(
      emptyUserAnswers.copy(ern = testNorthernIrelandErn).set(GuarantorRequiredPage, false).set(DestinationTypePage, MovementScenario.EuTaxWarehouse)
    )) {

      val expectedUserAnswers = emptyUserAnswers
        .copy(ern = testNorthernIrelandErn)
        .set(GuarantorRequiredPage, false)
        .set(DestinationTypePage, MovementScenario.EuTaxWarehouse)
        .set(HowMovementTransportedPage, HowMovementTransported.FixedTransportInstallations)
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onSubmit(testNorthernIrelandErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.RailTransport.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, UkTaxWarehouse.GB)(dataRequest(request, baseUserAnswers), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no destination type is found" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no destination type is found and an error exists" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }


    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must cleanse the journey section when changing the answer" in new Test(Some(
      baseUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.Other)
        .set(GiveInformationOtherTransportPage, "blah")
        .set(JourneyTimeDaysPage, 1)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "Container1")
        .set(TransportUnitTypePage(testIndex2), Tractor)
        .set(TransportUnitIdentityPage(testIndex2), "Tractor")
    )) {
      val expectedAnswers = baseUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.values.head)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "Container1")
        .set(TransportUnitTypePage(testIndex2), Tractor)
        .set(TransportUnitIdentityPage(testIndex2), "Tractor")

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must cleanse the journey and transport unit section when changing the answer (from fixed transport installations)" in new Test(Some(
      baseUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.FixedTransportInstallations)
        .set(TransportUnitTypePage(testIndex1), FixedTransport)
    )) {
      val expectedAnswers = baseUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.values.head)
        .set(TransportUnitsSection, Json.obj())

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to next page when answer unchanged" in new Test(Some(
      baseUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.SeaTransport)
        .set(JourneyTimeDaysPage, 1)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.SeaTransport.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }
  }
}
