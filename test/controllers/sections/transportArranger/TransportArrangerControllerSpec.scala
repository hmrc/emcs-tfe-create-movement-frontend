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
import forms.sections.transportArranger.TransportArrangerFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.transportArranger.TransportArranger
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.TransportArrangerPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportArranger.TransportArrangerView

import scala.concurrent.Future

class TransportArrangerControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  lazy val transportArrangerRoute = controllers.sections.transportArranger.routes.TransportArrangerController.onPageLoad(testErn, testLrn, NormalMode).url

  val formProvider = new TransportArrangerFormProvider()
  val form = formProvider()

  "TransportArranger Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportArrangerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportArrangerView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(TransportArrangerPage, TransportArranger.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportArrangerRoute)

        val view = application.injector.instanceOf[TransportArrangerView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(TransportArranger.values.head), NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[TransportArrangerNavigator].toInstance(new FakeTransportArrangerNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportArrangerRoute)
            .withFormUrlEncodedBody(("value", TransportArranger.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, transportArrangerRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[TransportArrangerView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, transportArrangerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, transportArrangerRoute)
            .withFormUrlEncodedBody(("value", TransportArranger.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
