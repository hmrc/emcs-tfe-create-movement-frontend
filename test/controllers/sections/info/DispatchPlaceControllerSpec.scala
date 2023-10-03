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
import forms.DispatchPlaceFormProvider
import mocks.services.MockUserAnswersService
import models.{DispatchPlace, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.info.DispatchPlaceView

class DispatchPlaceControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    val view = application.injector.instanceOf[DispatchPlaceView]
  }

  val formProvider = new DispatchPlaceFormProvider()
  val form = formProvider()

  lazy val dispatchPlaceRoute = controllers.sections.info.routes.DispatchPlaceController.onPageLoad(testErn).url
  lazy val dispatchPlaceSubmitAction = controllers.sections.info.routes.DispatchPlaceController.onSubmit(testErn)

  "DispatchPlace Controller" - {

    ".onPageLoad()" - {

      "must return OK and the correct view for a GET" in new Fixture() {
        running(application) {

          val request = FakeRequest(GET, dispatchPlaceRoute)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, dispatchPlaceSubmitAction)(userRequest(request), messages(application)).toString
        }
      }
    }

    ".onSubmit()" - {

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
        running(application) {

          val request = FakeRequest(POST, dispatchPlaceSubmitAction.url).withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, dispatchPlaceSubmitAction)(userRequest(request), messages(application)).toString
        }
      }

      // TODO Redirect to CAM-INFO08 once built
      "must redirect to the next page when valid data is submitted" in new Fixture() {
        running(application) {

          val validDispatchPlaceValue = DispatchPlace.values.head.toString

          val request = FakeRequest(POST, dispatchPlaceSubmitAction.url).withFormUrlEncodedBody(("value", validDispatchPlaceValue))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.sections.info.routes.DeferredMovementController.onPageLoad(testErn).url
        }
      }

    }

  }
}
