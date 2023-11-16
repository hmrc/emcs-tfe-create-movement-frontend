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
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import handlers.ErrorHandler
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockConsigneeCheckYourAnswersHelper
import models.UserAnswers
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExemptedOrganisation, GbTaxWarehouse}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.consignee._
import viewmodels.govuk.SummaryListFluency
import views.html.sections.consignee.CheckYourAnswersConsigneeView

class CheckYourAnswersConsigneeControllerSpec extends SpecBase with SummaryListFluency
  with MockConsigneeCheckYourAnswersHelper with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers]) {

    val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())
    val msgs = messages(testDataRequest)

    val ernList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true)(testDataRequest, msgs),
      ConsigneeExciseSummary.row(showActionLinks = true)(testDataRequest, msgs),
      ConsigneeAddressSummary.row(showActionLinks = true)(testDataRequest, msgs)
    ).flatten

    val exemptedList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true)(testDataRequest, msgs),
      ConsigneeExemptOrganisationSummary.row(showActionLinks = true)(testDataRequest, msgs),
      ConsigneeAddressSummary.row(showActionLinks = true)(testDataRequest, msgs)
    ).flatten

    val vatEoriList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true)(testDataRequest, msgs),
      ConsigneeExportVatSummary.row(showActionLinks = true)(testDataRequest, msgs),
      ConsigneeAddressSummary.row(showActionLinks = true)(testDataRequest, msgs)
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


    lazy val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
    val view: CheckYourAnswersConsigneeView = app.injector.instanceOf[CheckYourAnswersConsigneeView]

    object TestController extends CheckYourAnswersConsigneeController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents,
      new FakeConsigneeNavigator(testOnwardRoute),
      mockConsigneeCheckYourAnswersHelper,
      view
    )

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


        val result = TestController.onPageLoad(testErn, testDraftId)(testDataRequest)

        val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          ernSummaryList
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
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


        val result = TestController.onPageLoad(testErn, testDraftId)(testDataRequest)

        val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          exemptSummaryList
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
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


        val result = TestController.onPageLoad(testErn, testDraftId)(testDataRequest)

        val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          vatEoriSummaryList
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
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


        val result = TestController.onPageLoad(testErn, testDraftId)(testDataRequest)

        val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          vatEoriSummaryList
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {


        val result = TestController.onPageLoad(testErn, testDraftId)(testDataRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }

  ".onSubmit" - {
    "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {

      val result = TestController.onSubmit(testErn, testDraftId)(FakeRequest())

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe testOnwardRoute.url
    }
  }

}

