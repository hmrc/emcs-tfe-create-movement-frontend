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
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class ConsigneeAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {


  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val onwardRoute: Call = Call("GET", "/foo")
    val formProvider: AddressFormProvider = new AddressFormProvider()
    val form: Form[UserAddress] = formProvider()
    val view: AddressView = app.injector.instanceOf[AddressView]

    lazy val consigneeAddressOnSubmit: Call =
      controllers.sections.consignee.routes.ConsigneeAddressController.onSubmit(testErn, testDraftId, NormalMode)

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    object TestController extends ConsigneeAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(onwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      new AddressFormProvider(),
      messagesControllerComponents,
      view
    )
  }

  "ConsigneeAddress Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        addressPage = ConsigneeAddressPage,
        call = consigneeAddressOnSubmit
      )(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ConsigneeAddressPage, userAddressModelMax))) {

      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(userAddressModelMax),
        addressPage = ConsigneeAddressPage,
        call = consigneeAddressOnSubmit
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest().withFormUrlEncodedBody(
          ("property", userAddressModelMax.property.value),
          ("street", userAddressModelMax.street),
          ("town", userAddressModelMax.town),
          ("postcode", userAddressModelMax.postcode)
        )

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest().withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = ConsigneeAddressPage,
        call = consigneeAddressOnSubmit
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest().withFormUrlEncodedBody(("value", "answer"))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
