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
import forms.sections.destination.DestinationBusinessNameFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.DestinationNavigator
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination.DestinationBusinessNamePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.destination.DestinationBusinessNameView

import scala.concurrent.Future

class DestinationBusinessNameControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val formProvider = new DestinationBusinessNameFormProvider()
    val form = formProvider()

    lazy val destinationBusinessNameRoute = controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode).url
    lazy val destinationBusinessNameOnSubmit = controllers.sections.destination.routes.DestinationBusinessNameController.onSubmit(testErn, testDraftId, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[DestinationNavigator].toInstance(new FakeDestinationNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[DestinationBusinessNameView]
  }

  "DestinationBusinessName Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, destinationBusinessNameRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, destinationBusinessNameOnSubmit)(dataRequest(request), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(DestinationBusinessNamePage, "answer")
    )){

      running(application) {

        val request = FakeRequest(GET, destinationBusinessNameRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), destinationBusinessNameOnSubmit)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {

        val request =
          FakeRequest(POST, destinationBusinessNameRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {

        val request =
          FakeRequest(POST, destinationBusinessNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, destinationBusinessNameOnSubmit)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {

        val request = FakeRequest(GET, destinationBusinessNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {

        val request =
          FakeRequest(POST, destinationBusinessNameRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
