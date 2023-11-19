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

package controllers.sections.importInformation

import base.SpecBase
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockCheckYourAnswersImportHelper
import models.requests.DataRequest
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeImportInformationNavigator
import navigation.ImportInformationNavigator
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.importInformation.ImportCustomsOfficeCodeSummary
import viewmodels.govuk.SummaryListFluency
import viewmodels.helpers.CheckYourAnswersImportHelper
import views.html.sections.importInformation.CheckYourAnswersImportView

class CheckYourAnswersImportControllerSpec extends SpecBase with SummaryListFluency
  with MockCheckYourAnswersImportHelper with MockUserAnswersService {

  def request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, controllers.sections.importInformation.routes.CheckYourAnswersImportController.onPageLoad(testErn, testDraftId).url)

  implicit val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application: Application =
      applicationBuilder(userAnswers)
        .overrides(inject.bind[ImportInformationNavigator].toInstance(new FakeImportInformationNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[CheckYourAnswersImportHelper].toInstance(MockCheckYourAnswersImportHelper))
        .build()

    implicit val msgs: Messages = messages(request)


    val summaryList: SummaryList = SummaryListViewModel(
      rows = Seq(ImportCustomsOfficeCodeSummary.row(showActionLinks = true)).flatten
    ).withCssClass("govuk-!-margin-bottom-9")

    val view: CheckYourAnswersImportView = application.injector.instanceOf[CheckYourAnswersImportView]
  }

  "CheckYourAnswersImportController" - {
    ".onPageLoad" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, controllers.sections.importInformation.routes.CheckYourAnswersImportController.onPageLoad(testErn, testDraftId).url)

      "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456"))) {


        MockCheckAnswersImportHelper.summaryList().returns(summaryList)
        running(application) {

          val result = route(application, request).value

          val viewAsString = view(
            testErn,
            testDraftId,
            summaryList
          )(dataRequest(request), messages(request)).toString

          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString
        }
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect to /customs-office if user answers doesn't contain the correct page" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, NormalMode).url
        }
      }
    }

    ".onSubmit" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, controllers.sections.importInformation.routes.CheckYourAnswersImportController.onSubmit(testErn, testDraftId).url)

      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe testOnwardRoute.url
        }
      }
    }
  }
}

