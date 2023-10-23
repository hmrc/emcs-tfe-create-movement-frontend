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

package controllers.sections.transportArranger

import base.SpecBase
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.AddressView

import scala.concurrent.Future

class TransportArrangerAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val formProvider = new AddressFormProvider()
    val form = formProvider()

    lazy val transportArrangerAddressRoute = controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(testErn, testDraftId, NormalMode).url
    lazy val transportArrangerAddressOnSubmit = controllers.sections.transportArranger.routes.TransportArrangerAddressController.onSubmit(testErn, testDraftId, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[TransportArrangerNavigator].toInstance(new FakeTransportArrangerNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[AddressView]
  }

  "TransportArrangerAddress Controller" - {

    "when TransportArranger is GoodsOwner" - {

      "must return OK and the correct view for a GET" in new Fixture(
        Some(emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))
      ) {
        running(application) {

          val request = FakeRequest(GET, transportArrangerAddressRoute)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            addressPage = TransportArrangerAddressPage,
            call = transportArrangerAddressOnSubmit,
            headingKey = Some(s"$TransportArrangerAddressPage.$GoodsOwner")
          )(dataRequest(request), messages(application)).toString
        }
      }
    }

    "when TransportArranger is Other and already answered" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(
        emptyUserAnswers
          .set(TransportArrangerPage, Other)
          .set(TransportArrangerAddressPage, userAddressModelMax)
      )) {
        running(application) {

          val request = FakeRequest(GET, transportArrangerAddressRoute)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill(userAddressModelMax),
            addressPage = TransportArrangerAddressPage,
            call = transportArrangerAddressOnSubmit,
            headingKey = Some(s"$TransportArrangerAddressPage.$Other")
          )(dataRequest(request), messages(application)).toString
        }
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, transportArrangerAddressRoute)
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

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
      Some(emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))
    ) {

      running(application) {

        val request = FakeRequest(POST, transportArrangerAddressRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          addressPage = TransportArrangerAddressPage,
          call = transportArrangerAddressOnSubmit,
          headingKey = Some(s"$TransportArrangerAddressPage.$GoodsOwner")
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {

        val request = FakeRequest(GET, transportArrangerAddressRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {

        val request = FakeRequest(POST, transportArrangerAddressRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
