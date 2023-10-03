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
import controllers.routes
import forms.sections.consignee.ConsigneeExciseFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.DestinationType.TemporaryRegisteredConsignee
import models.{NormalMode, UserAnswers}
import navigation.ConsigneeNavigator
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExciseView

class ConsigneeExciseControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val formProvider = new ConsigneeExciseFormProvider()
    lazy val consigneeExciseRoute = controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testLrn, NormalMode).url
    lazy val consigneeExciseSubmit = controllers.sections.consignee.routes.ConsigneeExciseController.onSubmit(testErn, testLrn, NormalMode)

    lazy val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ConsigneeNavigator].toInstance(new FakeConsigneeNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
  }

  val userAnswersWithConsigneeExcise = emptyUserAnswers.set(ConsigneeExcisePage, testErn)
  val userAnswersWithDestinationType = emptyUserAnswers.set(DestinationTypePage, TemporaryRegisteredConsignee)

  "ConsigneeExciseController Controller" - {
    "must return OK and the correct view for a GET" - {
      "when Destination type is TemporaryRegisteredConsignee and Northern Irish" in new Fixture(Some(userAnswersWithDestinationType)) {

        running(application) {
          val request = FakeRequest(GET, consigneeExciseRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConsigneeExciseView]

          val form = formProvider(true)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = true)(dataRequest(request), messages(application)).toString
        }
      }
    }

    "when Destination type is NOT TemporaryRegisteredConsignee and Northern Irish" in new Fixture() {
      running(application) {
        val request = FakeRequest(GET, consigneeExciseRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ConsigneeExciseView]

        val form = formProvider(isNorthernIrishTemporaryRegisteredConsignee = false)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(userAnswersWithConsigneeExcise)) {
      running(application) {
        val request = FakeRequest(GET, consigneeExciseRoute)

        val view = application.injector.instanceOf[ConsigneeExciseView]

        val result = route(application, request).value

        val form = formProvider(isNorthernIrishTemporaryRegisteredConsignee = false)


        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(testErn), consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(userAnswersWithConsigneeExcise)) {

      running(application) {

        val request =
          FakeRequest(POST, consigneeExciseRoute)
            .withFormUrlEncodedBody(("value", testErn))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {
        val request =
          FakeRequest(POST, consigneeExciseRoute)
            .withFormUrlEncodedBody(("value", ""))

        val form = formProvider(isNorthernIrishTemporaryRegisteredConsignee = false)

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ConsigneeExciseView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {
        val request = FakeRequest(GET, consigneeExciseRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {
        val request =
          FakeRequest(POST, consigneeExciseRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}