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

package controllers

import base.SpecBase
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.ConsigneeAddressPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.AddressView

import scala.concurrent.Future

class ConsigneeAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {


  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val formProvider = new AddressFormProvider()
    val form = formProvider()

    lazy val consigneeAddressRoute = routes.ConsigneeAddressController.onPageLoad(testErn, testLrn, NormalMode).url
    lazy val consigneeAddressOnSubmit = routes.ConsigneeAddressController.onSubmit(testErn, testLrn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
  }


  "ConsigneeAddress Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(){

      running(application) {
        val request = FakeRequest(GET, consigneeAddressRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddressView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          addressPage = ConsigneeAddressPage,
          call = consigneeAddressOnSubmit
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(ConsigneeAddressPage, userAddressModelMax)
    )) {

      running(application) {
        val request = FakeRequest(GET, consigneeAddressRoute)

        val view = application.injector.instanceOf[AddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(userAddressModelMax),
          addressPage = ConsigneeAddressPage,
          call = consigneeAddressOnSubmit
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, consigneeAddressRoute)
            .withFormUrlEncodedBody(
              ("property", userAddressModelMax.property.value),
              ("street", userAddressModelMax.street),
              ("town", userAddressModelMax.town),
              ("postcode", userAddressModelMax.postcode)
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, consigneeAddressRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AddressView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          addressPage = ConsigneeAddressPage,
          call = consigneeAddressOnSubmit
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, consigneeAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, consigneeAddressRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
