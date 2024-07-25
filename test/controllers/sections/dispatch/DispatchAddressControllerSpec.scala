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

package controllers.sections.dispatch

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeDispatchNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.destination.DestinationAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchBusinessNamePage, DispatchUseConsignorDetailsPage}
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class DispatchAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  lazy val formProvider: AddressFormProvider = new AddressFormProvider()
  lazy val form: Form[UserAddress] = formProvider(DestinationAddressPage)(dataRequest(FakeRequest(), emptyUserAnswers))
  lazy val view: AddressView = app.injector.instanceOf[AddressView]

  def dispatchAddressRoute(ern: String = testErn): String =
    controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(ern, testDraftId, NormalMode).url
  def dispatchAddressOnSubmit(ern: String = testErn): Call =
    controllers.sections.dispatch.routes.DispatchAddressController.onSubmit(ern, testDraftId, NormalMode)
  def dispatchAddressOnSkip(ern: String = testErn): Call =
    controllers.sections.dispatch.routes.DispatchAddressController.onSkip(ern, testDraftId, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val testController = new DispatchAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeDispatchNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "DispatchAddress Controller" - {

    "must return OK and the correct view when NOT optional" in new Fixture(Some(emptyUserAnswers)) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testNICertifiedConsignorErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(ern = testNICertifiedConsignorErn),
        headingKey = Some("dispatchAddress")
      )(dataRequest(request, ern = testNICertifiedConsignorErn), messages(request)).toString
    }

    "must return OK and the correct view when optional" in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, UkTaxWarehouse.GB)
    )) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(ern = testGreatBritainWarehouseKeeperErn),
        onSkip = Some(dispatchAddressOnSkip(ern = testGreatBritainWarehouseKeeperErn)),
        headingKey = Some("dispatchAddress.optional")
      )(dataRequest(request, ern = testGreatBritainWarehouseKeeperErn), messages(request)).toString
    }

    "must fill the form with data from ConisgnorAddress when UseConsignor is true and no Dispatch address exists" in new Fixture(Some(
      emptyUserAnswers
        .set(DispatchUseConsignorDetailsPage, true)
        .set(ConsignorAddressPage, testUserAddress.copy(street = "Consignor"))
    )) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(testUserAddress.copy(street = "Consignor")),
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(),
        onSkip = Some(dispatchAddressOnSkip()),
        headingKey = Some("dispatchAddress.optional")
      )(dataRequest(request), messages(request)).toString
    }

    "must fill the form with data from DispatchAddress when UseConsignor is true and Dispatch address exists" in new Fixture(Some(
      emptyUserAnswers
        .set(DispatchUseConsignorDetailsPage, true)
        .set(ConsignorAddressPage, testUserAddress.copy(street = "Consignor"))
        .set(DispatchAddressPage, testUserAddress.copy(street = "Dispatch"))
    )) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(testUserAddress.copy(street = "Dispatch")),
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(),
        onSkip = Some(dispatchAddressOnSkip()),
        headingKey = Some("dispatchAddress.optional")
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req =
        FakeRequest(POST, dispatchAddressRoute())
          .withFormUrlEncodedBody(
            ("property", userAddressModelMax.property.value),
            ("street", userAddressModelMax.street),
            ("town", userAddressModelMax.town),
            ("postcode", userAddressModelMax.postcode)
          )

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      val request = FakeRequest(POST, dispatchAddressRoute()).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(),
        onSkip = Some(dispatchAddressOnSkip()),
        headingKey = Some("dispatchAddress.optional")
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page and wipe the answer for the current page when the question is skipped" in new Fixture(Some(emptyUserAnswers
      .set(DispatchBusinessNamePage, "name")
      .set(DispatchAddressPage, userAddressModelMax)
    )) {

      MockUserAnswersService
        .set(emptyUserAnswers.set(DispatchBusinessNamePage, "name"))
        .returns(Future.successful(emptyUserAnswers))

      val request = FakeRequest(GET, dispatchAddressOnSkip(testGreatBritainWarehouseKeeperErn).url)

      val result = testController.onSkip(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
    }

    "must redirect to Journey Recovery for a GET onPageLoad if no existing data is found" in new Fixture(None) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST onSubmit if no existing data is found" in new Fixture(None) {

      val req = FakeRequest(POST, dispatchAddressRoute()).withFormUrlEncodedBody(("value", "answer"))
      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a GET onSkip if no existing data is found" in new Fixture(None) {

      val request = FakeRequest(GET, dispatchAddressOnSkip().url)

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
