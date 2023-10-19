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
import fixtures.TransportUnitFixtures
import forms.sections.transportUnit.TransportSealTypeFormProvider
import mocks.services.MockUserAnswersService
import models.TransportUnitType.Container
import models.{NormalMode, TransportUnitType, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import navigation.TransportUnitNavigator
import pages.sections.transportUnit._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportSealTypeView

import scala.concurrent.Future

class TransportSealTypeControllerSpec extends SpecBase with MockUserAnswersService with TransportUnitFixtures {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val formProvider = new TransportSealTypeFormProvider()
    val form = formProvider()

    lazy val transportSealTypeRoute = routes.TransportSealTypeController.onPageLoad(testErn, testLrn, NormalMode).url

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[TransportUnitNavigator].toInstance(new FakeTransportUnitNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[TransportSealTypeView]
  }


  "TransportSealType Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage, Container)
    )) {

      running(application) {

        val request = FakeRequest(GET, transportSealTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          transportUnitType = TransportUnitType.Container,
          onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage, Container)
      .set(TransportSealTypePage, transportSealTypeModelMax)
    )) {
      running(application) {

        val request = FakeRequest(GET, transportSealTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(transportSealTypeModelMax),
          transportUnitType = TransportUnitType.Container,
          onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when only the seal type has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage, Container)
      .set(TransportSealTypePage, transportSealTypeModelMin)
    )) {
      running(application) {

        val request = FakeRequest(GET, transportSealTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(transportSealTypeModelMin),
          transportUnitType = TransportUnitType.Container,
          onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }


    "must redirect to journey recovery when no answer has been provided for transport unit type onPageLoad" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, transportSealTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage, Container)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, transportSealTypeRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage, Container)
    )) {

      running(application) {
        val request =
          FakeRequest(POST, transportSealTypeRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          transportUnitType = TransportUnitType.Container,
          onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testLrn, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to journey recovery when no answer has been provided for transport unit type onSubmit" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, transportSealTypeRoute)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, transportSealTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, transportSealTypeRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}