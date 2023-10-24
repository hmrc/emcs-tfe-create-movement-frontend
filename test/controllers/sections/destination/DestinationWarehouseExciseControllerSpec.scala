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

package controllers.sections.destination

import base.SpecBase
import controllers.routes
import forms.sections.destination.DestinationWarehouseExciseFormProvider
import mocks.services.MockUserAnswersService
import models.DispatchPlace.GreatBritain
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.{FakeDestinationNavigator, FakeNavigator}
import navigation.{DestinationNavigator, Navigator}
import models.sections.info.movementScenario.MovementScenario._
import pages.DispatchPlacePage
import pages.sections.destination.DestinationWarehouseExcisePage
import pages.sections.info.DestinationTypePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.destination.DestinationWarehouseExciseView

import scala.concurrent.Future

class DestinationWarehouseExciseControllerSpec extends SpecBase with MockUserAnswersService {


  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val formProvider = new DestinationWarehouseExciseFormProvider()
    val form = formProvider()

    lazy val destinationWarehouseExciseRoute = controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(testErn, testLrn, NormalMode).url
    lazy val destinationWarehouseExciseOnSubmit = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testLrn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[DestinationNavigator].toInstance(new FakeDestinationNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
    val view = application.injector.instanceOf[DestinationWarehouseExciseView]

  }

  "DestinationWarehouseExcise Controller" - {

    "must return OK and the correct view for a GET"  in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, DirectDelivery))) {

      running(application) {
        val request = FakeRequest(GET, destinationWarehouseExciseRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DestinationWarehouseExciseView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered"  in new Fixture(Some(emptyUserAnswers
      .set(DestinationWarehouseExcisePage, "answer").set(DestinationTypePage, DirectDelivery))) {


      running(application) {
        val request = FakeRequest(GET, destinationWarehouseExciseRoute)

        val view = application.injector.instanceOf[DestinationWarehouseExciseView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"),
          onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, DirectDelivery))) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))


      running(application) {
        val request =
          FakeRequest(POST, destinationWarehouseExciseRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(DestinationTypePage, DirectDelivery))) {


      running(application) {
        val request =
          FakeRequest(POST, destinationWarehouseExciseRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DestinationWarehouseExciseView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm,
          onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to destination type Page for a GET if the destination type value is invalid/none for this controller/page" in new Fixture(Some(emptyUserAnswers
      .set(DispatchPlacePage, GreatBritain))) {

      running(application) {
        val request = FakeRequest(GET, destinationWarehouseExciseRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {


      running(application) {
        val request = FakeRequest(GET, destinationWarehouseExciseRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, destinationWarehouseExciseRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
