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
import controllers.routes
import mocks.services.MockUserAnswersService
import models.UserAnswers
import navigation.DispatchNavigator
import navigation.FakeNavigators.FakeDispatchNavigator
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.FluentSummaryList
import views.html.sections.dispatch.DispatchCheckAnswersView

class DispatchCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService {


  class Test(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    lazy val dispatchCheckAnswersRoute = controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId).url

    lazy val application =
      applicationBuilder(userAnswers = userAnswers)
        .overrides(
          bind[DispatchNavigator].toInstance(new FakeDispatchNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view = application.injector.instanceOf[DispatchCheckAnswersView]

  }


  "DispatchCheckAnswers Controller" - {

    "must return OK and the correct view for a GET" in new Test() {

      running(application) {
        val request = FakeRequest(GET, dispatchCheckAnswersRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = SummaryList(Seq.empty).withCssClass("govuk-!-margin-bottom-9"),
          onSubmitCall = controllers.sections.dispatch.routes.DispatchCheckAnswersController.onSubmit(testErn, testDraftId)
        )(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Test() {

      running(application) {
        val request =
          FakeRequest(POST, dispatchCheckAnswersRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      running(application) {
        val request = FakeRequest(GET, dispatchCheckAnswersRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      running(application) {
        val request =
          FakeRequest(POST, dispatchCheckAnswersRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
