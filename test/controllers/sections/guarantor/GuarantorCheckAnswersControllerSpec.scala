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

package controllers.sections.guarantor

import base.SpecBase
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockGuarantorCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeGuarantorNavigator
import navigation.GuarantorNavigator
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.guarantor.GuarantorCheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.sections.guarantor.GuarantorCheckAnswersView

class GuarantorCheckAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockGuarantorCheckAnswersHelper with MockUserAnswersService {

  class Fixtures(userAnswers: Option[UserAnswers]) {

    lazy val checkYourAnswersJourneyTypeRoute = controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url

    lazy val view = application.injector.instanceOf[GuarantorCheckAnswersView]

    val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GuarantorCheckAnswersHelper].toInstance(MockGuarantorCheckAnswersHelper)
      )
      .build()
  }

  "GuarantorCheckAnswers Controller" - {

    "must return OK and the correct view for a GET" in new Fixtures(Some(emptyUserAnswers)){

      running(application) {

        implicit val request = dataRequest(FakeRequest(GET, checkYourAnswersJourneyTypeRoute))

        MockCheckAnswersJourneyTypeHelper.summaryList().returns(list)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = list,
          submitAction = controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onSubmit(testErn, testDraftId)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixtures(Some(emptyUserAnswers)){

      running(application) {
        val request =
          FakeRequest(POST, checkYourAnswersJourneyTypeRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixtures(None) {

      running(application) {
        val request = FakeRequest(GET, checkYourAnswersJourneyTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixtures(None) {

      running(application) {
        val request =
          FakeRequest(POST, checkYourAnswersJourneyTypeRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
