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
import forms.sections.transportUnit.TransportUnitIdentityFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, TransportUnitType}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import navigation.TransportUnitNavigator
import pages.{TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitIdentityView

import scala.concurrent.Future

class TransportUnitIdentityControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TransportUnitIdentityFormProvider()
  val form = formProvider(TransportUnitType.FixedTransport)

  lazy val transportUnitTransportIdentityRoute =
    controllers.sections.transportUnit.routes.TransportUnitIdentityController.onPageLoad(testErn, testLrn, NormalMode).url

  "TransportUnitIdentity Controller" - {

    "must return OK and the correct view for a GET if Transport unit type is answered" in {
      val userAnswers = emptyUserAnswers.set(TransportUnitTypePage, TransportUnitType.FixedTransport)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitTransportIdentityRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportUnitIdentityView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, TransportUnitType.FixedTransport, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered and transport unit type is answered" in {

      val userAnswers = emptyUserAnswers
        .set(TransportUnitIdentityPage, "answer")
        .set(TransportUnitTypePage, TransportUnitType.FixedTransport)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitTransportIdentityRoute)

        val view = application.injector.instanceOf[TransportUnitIdentityView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), TransportUnitType.FixedTransport, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TransportUnitTypePage, TransportUnitType.FixedTransport)

      MockUserAnswersService.set().returns(Future.successful(userAnswers))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TransportUnitNavigator].toInstance(new FakeTransportUnitNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitTransportIdentityRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to TransportUnitTypePage when nno TrasnportUnitType has been answered" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(GET, transportUnitTransportIdentityRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe
          controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.lrn, NormalMode).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TransportUnitTypePage, TransportUnitType.FixedTransport)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitTransportIdentityRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TransportUnitIdentityView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, TransportUnitType.FixedTransport, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitTransportIdentityRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitTransportIdentityRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
