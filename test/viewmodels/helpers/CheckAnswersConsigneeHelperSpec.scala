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

package viewmodels.helpers

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import models.UserAnswers
import models.requests.{DataRequest, UserRequest}
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, VatNumber}
import pages.sections.consignee._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.consignee._
import views.html.components.list

class CheckAnswersConsigneeHelperSpec extends SpecBase with MovementSubmissionFailureFixtures {

  lazy val list: list = app.injector.instanceOf[list]
  lazy val consigneeExciseSummary: ConsigneeExciseSummary = app.injector.instanceOf[ConsigneeExciseSummary]

  class Setup(ern: String = testErn, userAnswers: UserAnswers = emptyUserAnswers) {

    lazy val checkAnswersConsigneeHelper = new ConsigneeCheckAnswersHelper(list, consigneeExciseSummary)
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers.copy(ern = ern))
    implicit val testUserRequest: UserRequest[AnyContentAsEmpty.type] = userRequest(fakeDataRequest)
    implicit val msgs: Messages = stubMessagesApi().preferred(fakeDataRequest)
  }

  "CheckAnswersConsigneeHelper" - {

    ".buildSummaryRows should return the correct rows when" - {
      val testGbWarehouseErn = "GB00123456789"
      val testNiWarehouseErn = "XI00123456789"
      val testGbrcWarehouseErn = "GBRC123456789"
      val testXircWarehouseErn = "XIRC123456789"

      val warehouseUserAnswers = {
        emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExcisePage, testGbWarehouseErn)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val ernNeedsUpdatingUserAnswers = {
        emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExcisePage, testErn)
          .set(ConsigneeAddressPage, testUserAddress)
          .copy(submissionFailures = Seq(consigneeExciseFailure))
      }
      val exemptDataUserAnswers = {
        emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExemptOrganisationPage, testExemptedOrganisation)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val vatNumberUserAnswers = {
        emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExportInformationPage, Set(VatNumber))
          .set(ConsigneeExportVatPage, testVat)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val eoriUserAnswers = {
        emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExportInformationPage, Set(EoriNumber))
          .set(ConsigneeExportEoriPage, testEori)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val noVatNumberOrEoriUserAnswers = {
        emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeAddressPage, testUserAddress)
      }

      "the user type is GreatBritainWarehouse" in new Setup(testGbWarehouseErn, warehouseUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user type is NorthernIrelandWarehouse" in new Setup(testNiWarehouseErn, warehouseUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the destination type is DirectDelivery" in new Setup(testGbWarehouseErn, warehouseUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user type is GreatBritainRegisteredConsignor & destination type is TemporaryRegisteredConsignee" in
        new Setup(testGbrcWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is GreatBritainRegisteredConsignor & destination type is RegisteredConsignee" in
        new Setup(testGbrcWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is GreatBritainRegisteredConsignor & destination type is Export with dec in Uk" in
        new Setup(testGbrcWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is NorthernIrelandRegisteredConsignor & destination type is RegisteredConsignee" in
        new Setup(testXircWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is NorthernIrelandRegisteredConsignor & destination type is Export with dec in Eu" in
        new Setup(testXircWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      //TODO comeback and fix this, passing when it shouldn't?
      "the Consignee ERN needs to be updated" in new Setup(testGbWarehouseErn, ernNeedsUpdatingUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          consigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the destination type is ExemptedOrganisations" in new Setup(testGbWarehouseErn, exemptDataUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExemptOrganisationSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected yes VAT number" in new Setup(testGbWarehouseErn, vatNumberUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExportInformationSummary(list).row()(fakeDataRequest, msgs),
          ConsigneeExportVatSummary.row(showActionLinks = true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected Yes Eori number" in new Setup(testGbWarehouseErn, eoriUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExportInformationSummary(list).row()(fakeDataRequest, msgs),
          ConsigneeExportEoriSummary.row(showActionLinks = true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected no VAT or Eori number" in new Setup(testGbWarehouseErn, noVatNumberOrEoriUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }
    }

  }
}
