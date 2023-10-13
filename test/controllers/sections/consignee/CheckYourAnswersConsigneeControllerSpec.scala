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

package controllers.sections.consignee

import base.SpecBase
import controllers.routes
import handlers.ErrorHandler
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockConsigneeCheckYourAnswersHelper
import models.UserAnswers
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExemptedOrganisation, GbTaxWarehouse}
import navigation.ConsigneeNavigator
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.consignee._
import viewmodels.govuk.SummaryListFluency
import viewmodels.helpers.CheckYourAnswersConsigneeHelper
import views.html.sections.consignee.CheckYourAnswersConsigneeView

class CheckYourAnswersConsigneeControllerSpec extends SpecBase with SummaryListFluency
  with MockConsigneeCheckYourAnswersHelper with MockUserAnswersService {
  def request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testLrn).url)

  implicit val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)

  class Fixture(userAnswers: Option[UserAnswers]) {

    val application: Application =
      applicationBuilder(userAnswers)
        .overrides(inject.bind[ConsigneeNavigator].toInstance(new FakeConsigneeNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[CheckYourAnswersConsigneeHelper].toInstance(MockConsigneeCheckYourAnswersHelper)
        )
        .build()

    implicit val msgs: Messages = messages(application)

    val ernList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      ConsigneeExciseSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val exemptedList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      ConsigneeExemptOrganisationSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val vatEoriList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      ConsigneeExportVatSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val ernSummaryList: SummaryList = SummaryListViewModel(
      rows = ernList
    ).withCssClass("govuk-!-margin-bottom-9")

    val exemptSummaryList: SummaryList = SummaryListViewModel(
      rows = exemptedList
    ).withCssClass("govuk-!-margin-bottom-9")

    val vatEoriSummaryList: SummaryList = SummaryListViewModel(
      rows = vatEoriList
    ).withCssClass("govuk-!-margin-bottom-9")


    lazy val errorHandler: ErrorHandler = application.injector.instanceOf[ErrorHandler]
    val view: CheckYourAnswersConsigneeView = application.injector.instanceOf[CheckYourAnswersConsigneeView]
  }

  "Check Your Answers consignee Controller" - {
    ".onPageLoad" - {

      "must return OK and the correct view when supplied with ERN condition" in new Fixture(
        Some(
          emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExcisePage, testErn)
            .set(DestinationTypePage, GbTaxWarehouse)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(ernSummaryList)
        running(application) {

          val result = route(application, request).value

          val viewAsString = view(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testLrn),
            testErn,
            testLrn,
            ernSummaryList
          )(dataRequest(request), messages(application)).toString

          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString
        }
      }

      "must return OK and the correct view when supplied with Exempted Organisation condition" in new Fixture(
        Some(
          emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExemptOrganisationPage, testExemptedOrganisation)
            .set(DestinationTypePage, ExemptedOrganisation)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(exemptSummaryList)
        running(application) {

          val result = route(application, request).value

          val viewAsString = view(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testLrn),
            testErn,
            testLrn,
            exemptSummaryList
          )(dataRequest(request), messages(application)).toString

          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString
        }
      }

      "must return OK and the correct view when supplied with Eori number condition" in new Fixture(
        Some(
          emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExportVatPage, testEori)
            .set(DestinationTypePage, EuTaxWarehouse)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(vatEoriSummaryList)
        running(application) {

          val result = route(application, request).value

          val viewAsString = view(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testLrn),
            testErn,
            testLrn,
            vatEoriSummaryList
          )(dataRequest(request), messages(application)).toString

          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString
        }
      }

      "must return OK and the correct view when supplied with Vat number condition" in new Fixture(
        Some(
          emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExportVatPage, testVat)
            .set(DestinationTypePage, GbTaxWarehouse)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(vatEoriSummaryList)
        running(application) {

          val result = route(application, request).value

          val viewAsString = view(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testLrn),
            testErn,
            testLrn,
            vatEoriSummaryList
          )(dataRequest(request), messages(application)).toString

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
    }

    ".onSubmit" - {


      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {
        def request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(POST, controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testLrn).url)

        running(application) {


          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe testOnwardRoute.url
        }
      }
    }
  }
}

