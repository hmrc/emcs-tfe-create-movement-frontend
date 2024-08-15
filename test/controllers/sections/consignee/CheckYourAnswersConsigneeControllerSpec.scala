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
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockConsigneeCheckYourAnswersHelper
import models.UserAnswers
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, VatNumber}
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExemptedOrganisation, UkTaxWarehouse}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.consignee._
import viewmodels.govuk.SummaryListFluency
import views.html.components.list
import views.html.sections.consignee.CheckYourAnswersConsigneeView

class CheckYourAnswersConsigneeControllerSpec extends SpecBase with SummaryListFluency
  with MockConsigneeCheckYourAnswersHelper with MockUserAnswersService {

  lazy val view: CheckYourAnswersConsigneeView = app.injector.instanceOf[CheckYourAnswersConsigneeView]
  lazy val consigneeExciseSummary: ConsigneeExciseSummary = app.injector.instanceOf[ConsigneeExciseSummary]

  implicit val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(
    FakeRequest(GET, controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testLrn).url)
  )

  implicit val msgs: Messages = messages(testDataRequest)

  class Fixture(optUserAnswers: Option[UserAnswers]) {

    lazy val list: list = app.injector.instanceOf[list]

    val ernList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      consigneeExciseSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val exemptedList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      ConsigneeExemptOrganisationSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val vatList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      ConsigneeExportInformationSummary(list).row(),
      ConsigneeExportVatSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val eoriList: Seq[SummaryListRow] = Seq(
      ConsigneeBusinessNameSummary.row(showActionLinks = true),
      ConsigneeExportInformationSummary(list).row(),
      ConsigneeExportEoriSummary.row(showActionLinks = true),
      ConsigneeAddressSummary.row(showActionLinks = true)
    ).flatten

    val ernSummaryList: SummaryList = SummaryListViewModel(
      rows = ernList
    ).withCssClass("govuk-!-margin-bottom-9")

    val exemptSummaryList: SummaryList = SummaryListViewModel(
      rows = exemptedList
    ).withCssClass("govuk-!-margin-bottom-9")

    val vatSummaryList: SummaryList = SummaryListViewModel(
      rows = vatList
    ).withCssClass("govuk-!-margin-bottom-9")

    val eoriSummaryList: SummaryList = SummaryListViewModel(
      rows = eoriList
    ).withCssClass("govuk-!-margin-bottom-9")

    lazy val testController = new CheckYourAnswersConsigneeController(
      messagesApi,
      fakeAuthAction,
      fakeBetaAllowListAction,
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
            .set(DestinationTypePage, UkTaxWarehouse.GB)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(ernSummaryList)


        val result = testController.onPageLoad(testErn, testDraftId)(testDataRequest)

        lazy val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          Seq(ernSummaryList)
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


        val result = testController.onPageLoad(testErn, testDraftId)(testDataRequest)

        lazy val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          Seq(exemptSummaryList)
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }

      "must return OK and the correct view when supplied with Eori number condition" in new Fixture(
        Some(
          emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeExportEoriPage, testEori)
            .set(DestinationTypePage, EuTaxWarehouse)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(eoriSummaryList)


        val result = testController.onPageLoad(testErn, testDraftId)(testDataRequest)

        lazy val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          Seq(eoriSummaryList)
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }

      "must return OK and the correct view when supplied with Vat number condition" in new Fixture(
        Some(
          emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExportInformationPage, Set(VatNumber))
            .set(ConsigneeExportVatPage, testVatNumber)
            .set(DestinationTypePage, UkTaxWarehouse.GB)
        )) {

        MockConsigneeCheckAnswersHelper.summaryList().returns(vatSummaryList)


        val result = testController.onPageLoad(testErn, testDraftId)(testDataRequest)

        lazy val viewAsString = view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          Seq(vatSummaryList)
        )(testDataRequest, msgs).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {
        val result = testController.onPageLoad(testErn, testDraftId)(testDataRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }

  ".onSubmit" - {
    "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {
      val result = testController.onSubmit(testErn, testDraftId)(FakeRequest())

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe testOnwardRoute.url
    }
  }

}

