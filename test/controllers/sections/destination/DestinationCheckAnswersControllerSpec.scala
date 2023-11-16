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
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockDestinationCheckAnswersHelper
import models.UserAnswers
import models.sections.info.movementScenario.MovementScenario
import navigation.DestinationNavigator
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.info.DestinationTypePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.destination.DestinationCheckAnswersHelper
import viewmodels.govuk.all.FluentSummaryList
import viewmodels.govuk.summarylist._
import views.html.sections.destination.DestinationCheckAnswersView


class DestinationCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockDestinationCheckAnswersHelper {

  class Test(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    lazy val destinationCheckAnswersRoute =
      controllers.sections.destination.routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId).url

    lazy val destinationCheckAnswersOnSubmit =
      controllers.sections.destination.routes.DestinationCheckAnswersController.onSubmit(testErn, testDraftId)

    val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

    lazy val application =
      applicationBuilder(userAnswers = userAnswers)
        .overrides(
          bind[DestinationNavigator].toInstance(new FakeDestinationNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[DestinationCheckAnswersHelper].toInstance(mockDestinationCheckAnswersHelper)
        )
        .build()

    val view = application.injector.instanceOf[DestinationCheckAnswersView]

  }

  def onwardRoute = Call("GET", "/foo")


  "DestinationCheckAnswers Controller" - {

    "must return OK and the correct view for a GET when destination type has been answered" in new Test(Some(emptyUserAnswers
      .set(DestinationTypePage, MovementScenario.DirectDelivery)
    )) {

      running(application) {

        MockCheckAnswersJourneyTypeHelper.summaryList().returns(list)

        val request = FakeRequest(GET, destinationCheckAnswersRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = list,
          onSubmitCall = destinationCheckAnswersOnSubmit
        )(dataRequest(request), messages(request)).toString
      }
    }

    "must return OK and the correct view for a GET when destination type has NOT been answered" in new Test() {

      running(application) {

        val request = FakeRequest(GET, destinationCheckAnswersRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to the next page when submitted" in new Test() {

      running(application) {
        val request =
          FakeRequest(POST, destinationCheckAnswersRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      running(application) {
        val request = FakeRequest(GET, destinationCheckAnswersRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      running(application) {
        val request =
          FakeRequest(POST, destinationCheckAnswersRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
