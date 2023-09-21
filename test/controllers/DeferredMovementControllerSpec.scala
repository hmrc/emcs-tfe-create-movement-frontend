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

package controllers

import base.SpecBase
import forms.DeferredMovementFormProvider
import mocks.services.MockUserAnswersService
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.DeferredMovementView

class DeferredMovementControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture() {
    val application =
      applicationBuilder(userAnswers = None)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    val view = application.injector.instanceOf[DeferredMovementView]
  }
  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DeferredMovementFormProvider()
  val form = formProvider()

  lazy val deferredMovementRoute = controllers.sections.info.routes.DeferredMovementController.onPageLoad(testErn).url

  "DeferredMovement Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      running(application) {
        val request = FakeRequest(GET, deferredMovementRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, controllers.sections.info.routes.DeferredMovementController.onSubmit(testErn))(userRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, deferredMovementRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(testErn).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, deferredMovementRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, controllers.sections.info.routes.DeferredMovementController.onSubmit(testErn))(userRequest(request), messages(application)).toString
      }
    }
  }
}
