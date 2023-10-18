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

package controllers.sections.exportInformation

import base.SpecBase
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockCheckAnswersExportInformationHelper
import models.UserAnswers
import navigation.ExportInformationNavigator
import navigation.FakeNavigators.FakeExportInformationNavigator
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.exportInformation.ExportInformationCheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.sections.exportInformation.ExportInformationCheckAnswersView

class ExportInformationCheckAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockCheckAnswersExportInformationHelper with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  class Fixtures(userAnswers: Option[UserAnswers]) {

    lazy val checkYourAnswersExportInformationRoute =
      controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testLrn).url

    lazy val view = application.injector.instanceOf[ExportInformationCheckAnswersView]

    val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ExportInformationNavigator].toInstance(new FakeExportInformationNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[ExportInformationCheckAnswersHelper].toInstance(mockExportInformationCheckAnswersHelper)
      )
      .build()
  }

  "CheckYourAnswersExportInformation Controller" - {

    "must return OK and the correct view for a GET" in new Fixtures(Some(emptyUserAnswers)) {

      running(application) {

        implicit val request = dataRequest(FakeRequest(GET, checkYourAnswersExportInformationRoute))

        MockCheckAnswersExportInformationHelper.summaryList().returns(list)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = list,
          submitAction = controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onSubmit(testErn, testLrn)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixtures(Some(emptyUserAnswers)) {

      running(application) {
        val request = FakeRequest(POST, checkYourAnswersExportInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }
  }
}
