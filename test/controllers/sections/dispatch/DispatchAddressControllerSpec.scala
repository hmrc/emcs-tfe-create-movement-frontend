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
//import pages.sections.destination.DestinationAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchUseConsignorDetailsPage}
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class DispatchAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  lazy val formProvider: AddressFormProvider = new AddressFormProvider()

  def form(isConsignorPageOrUsingConsignorDetails: Boolean): Form[UserAddress] =
    formProvider(DispatchAddressPage, isConsignorPageOrUsingConsignorDetails)(dataRequest(FakeRequest(), emptyUserAnswers))

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
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
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
        form = form(isConsignorPageOrUsingConsignorDetails = false),
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(ern = testNICertifiedConsignorErn),
        headingKey = Some("dispatchAddress"),
        isConsignorPageOrUsingConsignorDetails = false
      )(dataRequest(request, ern = testNICertifiedConsignorErn), messages(request)).toString
    }

    "must return OK and the correct view when optional" in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, UkTaxWarehouse.GB)
    )) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form(isConsignorPageOrUsingConsignorDetails = false),
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(ern = testGreatBritainWarehouseKeeperErn),
        onSkip = Some(dispatchAddressOnSkip(ern = testGreatBritainWarehouseKeeperErn)),
        headingKey = Some("dispatchAddress.optional"),
        isConsignorPageOrUsingConsignorDetails = false
      )(dataRequest(request, ern = testGreatBritainWarehouseKeeperErn), messages(request)).toString
    }

    "must fill the form with data from ConsignorAddress when UseConsignor is true and no Dispatch address exists" in new Fixture(Some(
      emptyUserAnswers
        .set(DispatchUseConsignorDetailsPage, true)
        .set(ConsignorAddressPage, testUserAddress.copy(street = Some("Consignor")))
    )) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form(isConsignorPageOrUsingConsignorDetails = true).fill(testUserAddress.copy(street = Some("Consignor"))),
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(),
        onSkip = None,
        headingKey = None,
        isConsignorPageOrUsingConsignorDetails = true
      )(dataRequest(request), messages(request)).toString
    }

    "must fill the form with data from DispatchAddress when UseConsignor is true and Dispatch address exists" in new Fixture(Some(
      emptyUserAnswers
        .set(DispatchUseConsignorDetailsPage, true)
        .set(ConsignorAddressPage, testUserAddress.copy(street = Some("Consignor")))
        .set(DispatchAddressPage, testUserAddress.copy(street = Some("Dispatch")))
    )) {

      val request = FakeRequest(GET, dispatchAddressRoute())

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form(isConsignorPageOrUsingConsignorDetails = true).fill(testUserAddress.copy(street = Some("Dispatch"))),
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(),
        onSkip = None,
        headingKey = None,
        isConsignorPageOrUsingConsignorDetails = true
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req =
        FakeRequest(POST, dispatchAddressRoute())
          .withFormUrlEncodedBody(
            ("businessName", userAddressModelMax.businessName.value),
            ("property", userAddressModelMax.property.value),
            ("street", userAddressModelMax.street.value),
            ("town", userAddressModelMax.town.value),
            ("postcode", userAddressModelMax.postcode.value)
          )

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      val request = FakeRequest(POST, dispatchAddressRoute()).withFormUrlEncodedBody(("value", ""))
      val boundForm = form(isConsignorPageOrUsingConsignorDetails = false).bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = DispatchAddressPage,
        onSubmit = dispatchAddressOnSubmit(),
        onSkip = Some(dispatchAddressOnSkip()),
        headingKey = Some("dispatchAddress.optional"),
        isConsignorPageOrUsingConsignorDetails = false
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page and wipe the answer for the current page when the question is skipped" in new Fixture(Some(emptyUserAnswers
      .set(DispatchAddressPage, userAddressModelMax)
    )) {

      MockUserAnswersService
        .set(emptyUserAnswers.set(DispatchAddressPage, userAddressModelMax).remove(DispatchAddressPage))
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
