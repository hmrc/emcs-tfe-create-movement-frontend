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

package controllers.sections.consignor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeConsignorNavigator
import pages.sections.consignor.ConsignorAddressPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class ConsignorAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  lazy val formProvider: AddressFormProvider = new AddressFormProvider()
  lazy val form: Form[UserAddress] = formProvider(ConsignorAddressPage)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testGreatBritainErn))
  lazy val view: AddressView = app.injector.instanceOf[AddressView]

  lazy val consignorAddressRoute: String =
    controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode).url
  def consignorAddressOnSubmit(ern: String = testGreatBritainErn): Call =
    controllers.sections.consignor.routes.ConsignorAddressController.onSubmit(ern, testDraftId, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, consignorAddressRoute)

    lazy val testController = new ConsignorAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsignorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "ConsignorAddress Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        addressPage = ConsignorAddressPage,
        call = consignorAddressOnSubmit()
      )(dataRequest(request, ern = testGreatBritainErn), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(ConsignorAddressPage, userAddressModelMax)
    )) {
      val result = testController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(userAddressModelMax),
        addressPage = ConsignorAddressPage,
        call = consignorAddressOnSubmit()
      )(dataRequest(request, ern = testGreatBritainErn), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val req =
        FakeRequest(POST, consignorAddressRoute)
          .withFormUrlEncodedBody(
            ("property", userAddressModelMax.property.value),
            ("street", userAddressModelMax.street),
            ("town", userAddressModelMax.town),
            ("postcode", userAddressModelMax.postcode)
          )

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, consignorAddressRoute).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsignorAddressPage,
        call = consignorAddressOnSubmit()
      )(dataRequest(req, ern = testGreatBritainErn), messages(req)).toString
    }

    "must return a Bad Request and errors when a 'BT' postcode is entered for a GB trader" in new Fixture() {
      val req = FakeRequest(POST, consignorAddressRoute).withFormUrlEncodedBody(("value", "BT11AA"))

      val boundForm = form.bind(Map("value" -> "BT11AA"))

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsignorAddressPage,
        call = consignorAddressOnSubmit()
      )(dataRequest(req, ern = testGreatBritainErn), messages(req)).toString
    }

    "must return a Bad Request and errors when a GB postcode is entered for an XI trader" in new Fixture() {
      val req = FakeRequest(POST, consignorAddressRoute).withFormUrlEncodedBody(("value", "B11AA"))

      val boundForm = form.bind(Map("value" -> "B11AA"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsignorAddressPage,
        call = consignorAddressOnSubmit(testErn)
      )(dataRequest(req, ern = testErn), messages(req)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, consignorAddressRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}

