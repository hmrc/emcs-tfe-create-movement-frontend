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

package controllers.sections.consignee

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeAddressPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class ConsigneeAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {
  lazy val formProvider: AddressFormProvider = new AddressFormProvider()
  lazy val form: Form[UserAddress] = formProvider(ConsigneeAddressPage)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testGreatBritainErn))
  lazy val view: AddressView = app.injector.instanceOf[AddressView]

  lazy val consigneeAddressRoute: String =
    controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode).url

  def consigneeAddressOnSubmit(ern: String = testGreatBritainErn): Call =
    controllers.sections.consignee.routes.ConsigneeAddressController.onSubmit(ern, testDraftId, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, consigneeAddressRoute)

    lazy val testController = new ConsigneeAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "ConsigneeAddress Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        addressPage = ConsigneeAddressPage,
        onSubmit = consigneeAddressOnSubmit()
      )(dataRequest(request, ern = testGreatBritainErn), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(ConsigneeAddressPage, userAddressModelMax)
    )) {
      val result = testController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(userAddressModelMax),
        addressPage = ConsigneeAddressPage,
        onSubmit = consigneeAddressOnSubmit()
      )(dataRequest(request, ern = testGreatBritainErn), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val req =
        FakeRequest(POST, consigneeAddressRoute)
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
      val req = FakeRequest(POST, consigneeAddressRoute).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsigneeAddressPage,
        onSubmit = consigneeAddressOnSubmit()
      )(dataRequest(req, ern = testGreatBritainErn), messages(req)).toString
    }

    "must return a Bad Request and errors when a 'BT' postcode is entered for a GB trader" in new Fixture() {
      val req = FakeRequest(POST, consigneeAddressRoute).withFormUrlEncodedBody(("value", "BT11AA"))

      val boundForm = form.bind(Map("value" -> "BT11AA"))

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsigneeAddressPage,
        onSubmit = consigneeAddressOnSubmit()
      )(dataRequest(req, ern = testGreatBritainErn), messages(req)).toString
    }

    "must return a Bad Request and errors when a GB postcode is entered for an XI trader" in new Fixture() {
      val req = FakeRequest(POST, consigneeAddressRoute).withFormUrlEncodedBody(("value", "B11AA"))

      val boundForm = form.bind(Map("value" -> "B11AA"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsigneeAddressPage,
        onSubmit = consigneeAddressOnSubmit(testErn)
      )(dataRequest(req, ern = testErn), messages(req)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testGreatBritainErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, consigneeAddressRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testGreatBritainErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
