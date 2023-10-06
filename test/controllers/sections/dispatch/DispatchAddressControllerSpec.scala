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
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDispatchNavigator
import navigation.DispatchNavigator
import pages.sections.dispatch.DispatchAddressPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.AddressView

import scala.concurrent.Future

class DispatchAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val formProvider = new AddressFormProvider()
    val form = formProvider()

    lazy val dispatchAddressRoute = controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(testErn, testLrn, NormalMode).url
    lazy val dispatchAddressOnSubmit = controllers.sections.dispatch.routes.DispatchAddressController.onSubmit(testErn, testLrn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[DispatchNavigator].toInstance(new FakeDispatchNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[AddressView]
  }

  "DispatchAddress Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
      running(application) {

        val request = FakeRequest(GET, dispatchAddressRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          addressPage = DispatchAddressPage,
          call = dispatchAddressOnSubmit,
          headingKey = Some("dispatchAddress")
        )(dataRequest(request), messages(application)).toString
      }
    }


    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, dispatchAddressRoute)
            .withFormUrlEncodedBody(
              ("property", userAddressModelMax.property.value),
              ("street", userAddressModelMax.street),
              ("town", userAddressModelMax.town),
              ("postcode", userAddressModelMax.postcode)
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      running(application) {

        val request = FakeRequest(POST, dispatchAddressRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          addressPage = DispatchAddressPage,
          call = dispatchAddressOnSubmit,
          headingKey = Some("dispatchAddress")
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {

        val request = FakeRequest(GET, dispatchAddressRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {

        val request = FakeRequest(POST, dispatchAddressRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
