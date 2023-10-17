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

package controllers.sections.transportUnit

import base.SpecBase
import controllers.routes
import forms.sections.transportUnit.TransportUnitGiveMoreInformationFormProvider
import mocks.services.MockUserAnswersService
import models.TransportUnitType.Tractor
import models.{NormalMode, TransportUnitType}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.sections.transportUnit.{TransportUnitGiveMoreInformationPage, TransportUnitTypePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationView

import scala.concurrent.Future

class TransportUnitGiveMoreInformationControllerSpec extends SpecBase with MockUserAnswersService {

  //TODO add correct URL for CAM-TU07
  val onwardRoute: Call = Call("GET", "/emcs/create-movement/test-only/construction")

  val formProvider = new TransportUnitGiveMoreInformationFormProvider()

  lazy val transportUnitGiveMoreInformationRoute = controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testLrn, NormalMode).url

  "TransportUnitGiveMoreInformation Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage, Tractor))).build()
      def form(transportUnitType: TransportUnitType): Form[String] = formProvider(transportUnitType)(messages(application))

      running(application) {
        val request = FakeRequest(GET, transportUnitGiveMoreInformationRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportUnitGiveMoreInformationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form(Tractor), NormalMode, Tractor)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationPage, "answer").set(TransportUnitTypePage, Tractor)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      def form(transportUnitType: TransportUnitType): Form[String] = formProvider(transportUnitType)(messages(application))

      running(application) {
        val request = FakeRequest(GET, transportUnitGiveMoreInformationRoute)

        val view = application.injector.instanceOf[TransportUnitGiveMoreInformationView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form(Tractor).fill("answer"), NormalMode, Tractor)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect back to TU01 for a GET if there is not a transport unit type found in the users answers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitGiveMoreInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testLrn, NormalMode).url
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers.set(TransportUnitTypePage, Tractor).set(TransportUnitGiveMoreInformationPage, "answer")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage, Tractor)))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitGiveMoreInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage, Tractor))).build()
      def form(transportUnitType: TransportUnitType): Form[String] = formProvider(transportUnitType)(messages(application))

      running(application) {
        val request =
          FakeRequest(POST, transportUnitGiveMoreInformationRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(Tractor).bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TransportUnitGiveMoreInformationView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Tractor)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect back to TU01 for a POST if there is not a transport unit type found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitGiveMoreInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testLrn, NormalMode).url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitGiveMoreInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitGiveMoreInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
