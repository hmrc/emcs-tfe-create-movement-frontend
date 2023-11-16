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

package controllers.sections.journeyType

import base.SpecBase
import controllers.routes
import forms.sections.journeyType.HowMovementTransportedFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.journeyType.HowMovementTransported
import navigation.FakeNavigators.FakeJourneyTypeNavigator
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeDaysPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.journeyType.HowMovementTransportedView

import scala.concurrent.Future

class HowMovementTransportedControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  lazy val howMovementTransportedRoute = controllers.sections.journeyType.routes.HowMovementTransportedController.onPageLoad(testErn, testDraftId, NormalMode).url

  val formProvider = new HowMovementTransportedFormProvider()
  val form = formProvider()

  "HowMovementTransported Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, howMovementTransportedRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowMovementTransportedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(HowMovementTransportedPage, HowMovementTransported.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, howMovementTransportedRoute)

        val view = application.injector.instanceOf[HowMovementTransportedView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(HowMovementTransported.values.head), NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[JourneyTypeNavigator].toInstance(new FakeJourneyTypeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, howMovementTransportedRoute)
            .withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, howMovementTransportedRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[HowMovementTransportedView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, howMovementTransportedRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, howMovementTransportedRoute)
            .withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must cleanse the journey section when changing the answer" in {
      val expectedAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.values.head)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val application = applicationBuilder(
        userAnswers = Some(
          emptyUserAnswers
            .set(HowMovementTransportedPage, HowMovementTransported.Other )
            .set(GiveInformationOtherTransportPage, "blah")
            .set(JourneyTimeDaysPage, 1)
        )
      )
        .overrides(
          bind[JourneyTypeNavigator].toInstance(new FakeJourneyTypeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

      val request = FakeRequest(POST, howMovementTransportedRoute).withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString))
      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
    }

    "must redirect to next page when answer unchanged" in {
      val unchangingTransportMode = HowMovementTransported.SeaTransport
      val unchangingAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, unchangingTransportMode)
        .set(JourneyTimeDaysPage, 1)

      val application =
        applicationBuilder(userAnswers = Some(unchangingAnswers))
          .overrides(
            bind[JourneyTypeNavigator].toInstance(new FakeJourneyTypeNavigator(onwardRoute)),
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, howMovementTransportedRoute)
            .withFormUrlEncodedBody(("value", unchangingTransportMode.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }
  }
}
