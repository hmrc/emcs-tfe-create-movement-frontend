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

package controllers.sections.dispatch

import base.SpecBase
import forms.sections.dispatch.DispatchUseConsignorDetailsFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.DispatchNavigator
import navigation.FakeNavigators.FakeDispatchNavigator
import pages.sections.dispatch.DispatchUseConsignorDetailsPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.dispatch.DispatchUseConsignorDetailsView

import scala.concurrent.Future

class DispatchUseConsignorDetailsControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new DispatchUseConsignorDetailsFormProvider()
  val form = formProvider()

  lazy val dispatchUseConsignorDetailsRoute = routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val dispatchUseConsignorDetailsSubmitAction = routes.DispatchUseConsignorDetailsController.onSubmit(testErn, testDraftId, NormalMode)

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val application =
      applicationBuilder(userAnswers = userAnswers)
        .overrides(
          bind[DispatchNavigator].toInstance(new FakeDispatchNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view = application.injector.instanceOf[DispatchUseConsignorDetailsView]

  }

  "DispatchUseConsignorDetails Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
      running(application) {

        val request = FakeRequest(GET, dispatchUseConsignorDetailsRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, dispatchUseConsignorDetailsSubmitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true))
    ) {
      running(application) {

        val request = FakeRequest(GET, dispatchUseConsignorDetailsRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), dispatchUseConsignorDetailsSubmitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", "true"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
      running(application) {

        val request = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, dispatchUseConsignorDetailsSubmitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, dispatchUseConsignorDetailsRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", "true"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
