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

package views.sections.consignee

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.TaskListStatusMessages
import fixtures.messages.sections.consignee.CheckYourAnswersConsigneeMessages.English
import models.{CheckMode, NormalMode}
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, VatNumber}
import models.sections.info.movementScenario.MovementScenario._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import utils._
import viewmodels.checkAnswers.sections.consignee._
import views.html.components.list
import views.html.sections.consignee.CheckYourAnswersConsigneeView
import views.{BaseSelectors, ViewBehaviours}

class CheckYourAnswersConsigneeViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  lazy val consigneeExciseSummary: ConsigneeExciseSummary = app.injector.instanceOf[ConsigneeExciseSummary]

  lazy val view = app.injector.instanceOf[CheckYourAnswersConsigneeView]

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"
    val govukSummaryListChangeLink = ".govuk-summary-list__actions .govuk-link"
    val tag = ".govuk-tag--orange"
  }

  "CheckYourAnswersConsignee view" - {

    s"when being rendered in lang code of '${English.lang.code}' for ERN'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      implicit val request: DataRequest[AnyContentAsEmpty.type] =
        dataRequest(FakeRequest(), emptyUserAnswers
          .set(ConsigneeAddressPage, testUserAddress)
          .set(ConsigneeBusinessNamePage, testBusinessName)
          .set(ConsigneeExcisePage, testErn)
          .set(DestinationTypePage, UkTaxWarehouse.GB)
        )

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          ConsigneeBusinessNameSummary.row(true),
          consigneeExciseSummary.row(true),
          ConsigneeAddressSummary.row(true)
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.caption,
        Selectors.govukSummaryListKey(1) -> English.traderName,
        Selectors.govukSummaryListKey(2) -> English.ern,
        Selectors.govukSummaryListKey(3) -> English.address,
        Selectors.button -> English.confirmAnswers,
      ))

      "have a link to change business name" in {
        doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change ERN" in {
        doc.getElementById("changeConsigneeExcise").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Address" in {
        doc.getElementById("changeConsigneeAddress").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' for ERN with an ERN Error'" - {
      implicit val msgs: Messages = messages(Seq(English.lang))

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
        emptyUserAnswers.copy(submissionFailures =
          ConsigneeExcisePage.possibleErrors.map(error => consigneeExciseFailure.copy(error.code))
        )
        .set(ConsigneeExcisePage, testErn)
        .set(DestinationTypePage, UkTaxWarehouse.GB)
      )

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          consigneeExciseSummary.row(true),
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.subHeadingCaptionSelector -> English.caption,
        Selectors.notificationBannerTitle -> English.updateNeeded,
        Selectors.govukSummaryListKey(1) -> English.ern,
        Selectors.tag -> TaskListStatusMessages.English.updateNeededTag,
        Selectors.submissionError(InvalidOrMissingConsigneeError) -> English.invalidOrMissingConsignee,
        Selectors.submissionError(LinkIsPendingError) -> English.linkIsPending,
        Selectors.submissionError(LinkIsAlreadyUsedError) -> English.linkIsAlreadyUsed,
        Selectors.submissionError(LinkIsWithdrawnError) -> English.linkIsWithdrawn,
        Selectors.submissionError(LinkIsCancelledError) -> English.linkIsCancelled,
        Selectors.submissionError(LinkIsExpiredError) -> English.linkIsExpired,
        Selectors.submissionError(LinkMissingOrInvalidError) -> English.linkMissingOrInvalid,
        Selectors.submissionError(DirectDeliveryNotAllowedError) -> English.directDeliveryNotAllowed,
        Selectors.submissionError(ConsignorNotAuthorisedError) -> English.consignorNotAuthorised,
        Selectors.submissionError(RegisteredConsignorToRegisteredConsigneeError) -> English.registeredConsignorToRegisteredConsignee,
        Selectors.submissionError(ConsigneeRoleInvalidError) -> English.consigneeRoleInvalid,
        Selectors.button -> English.confirmAnswers,
      ))

      "link to the consignee excise page" in {
        val route = controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url
        ConsigneeExcisePage.possibleErrors.foreach(
          error => doc.select(Selectors.submissionError(error)).attr("href") mustBe route
        )
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' for Identification number for Temporary Registered Consignee'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      implicit val request: DataRequest[AnyContentAsEmpty.type] =
        dataRequest(FakeRequest(), emptyUserAnswers
          .set(ConsigneeAddressPage, testUserAddress)
          .set(ConsigneeBusinessNamePage, testBusinessName)
          .set(ConsigneeExcisePage, testErn)
          .set(DestinationTypePage, TemporaryRegisteredConsignee)
        )

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          ConsigneeBusinessNameSummary.row(true),
          consigneeExciseSummary.row(true),
          ConsigneeAddressSummary.row(true)
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.caption,
        Selectors.govukSummaryListKey(1) -> English.traderName,
        Selectors.govukSummaryListKey(2) -> English.ernNumberForTemporaryRegisteredConsignee,
        Selectors.govukSummaryListKey(3) -> English.address,
        Selectors.button -> English.confirmAnswers,
      ))

      "have a link to change business name" in {
        doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change ERN" in {
        doc.getElementById("changeConsigneeExcise").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Address" in {
        doc.getElementById("changeConsigneeAddress").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' for Identification number for Temporary Certified Consignee'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      implicit val request: DataRequest[AnyContentAsEmpty.type] =
        dataRequest(FakeRequest(), emptyUserAnswers
          .set(ConsigneeAddressPage, testUserAddress)
          .set(ConsigneeBusinessNamePage, testBusinessName)
          .set(ConsigneeExcisePage, testErn)
          .set(DestinationTypePage, TemporaryCertifiedConsignee)
        )

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          ConsigneeBusinessNameSummary.row(true),
          consigneeExciseSummary.row(true),
          ConsigneeAddressSummary.row(true)
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.caption,
        Selectors.govukSummaryListKey(1) -> English.traderName,
        Selectors.govukSummaryListKey(2) -> English.ernNumberForTemporaryCertifiedConsignee,
        Selectors.govukSummaryListKey(3) -> English.address,
        Selectors.button -> English.confirmAnswers,
      ))

      "have a link to change business name" in {
        doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change ERN" in {
        doc.getElementById("changeConsigneeExcise").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Address" in {
        doc.getElementById("changeConsigneeAddress").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' for Exempted Organisation'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] =
        dataRequest(FakeRequest(), emptyUserAnswers
          .set(ConsigneeAddressPage, testUserAddress)
          .set(ConsigneeBusinessNamePage, testBusinessName)
          .set(ConsigneeExcisePage, testErn)
          .set(ConsigneeExemptOrganisationPage, testExemptedOrganisation)
          .set(DestinationTypePage, ExemptedOrganisation)
        )

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          ConsigneeBusinessNameSummary.row(true),
          ConsigneeExemptOrganisationSummary.row(true),
          ConsigneeAddressSummary.row(true)
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.caption,
        Selectors.govukSummaryListKey(1) -> English.traderName,
        Selectors.govukSummaryListKey(2) -> English.exempt,
        Selectors.govukSummaryListKey(3) -> English.address,
        Selectors.button -> English.confirmAnswers,
      ))

      "have a link to change business name" in {
        doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Exempted Organisation" in {
        doc.getElementById("changeConsigneeExemptOrganisation").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Address" in {
        doc.getElementById("changeConsigneeAddress").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' for Vat'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] =
        dataRequest(FakeRequest(), emptyUserAnswers
          .set(ConsigneeAddressPage, testUserAddress)
          .set(ConsigneeBusinessNamePage, testBusinessName)
          .set(ConsigneeExcisePage, testErn)
          .set(ConsigneeExportInformationPage, Set(VatNumber))
          .set(ConsigneeExportVatPage, testVat)
          .set(DestinationTypePage, UkTaxWarehouse.GB)
        )

      lazy val list: list = app.injector.instanceOf[list]

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          ConsigneeBusinessNameSummary.row(true),
          ConsigneeExportInformationSummary(list).row(),
          ConsigneeExportVatSummary.row(true),
          ConsigneeAddressSummary.row(true)
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.caption,
        Selectors.govukSummaryListKey(1) -> English.traderName,
        Selectors.govukSummaryListKey(2) -> English.identificationProvided,
        Selectors.govukSummaryListKey(3) -> English.vat,
        Selectors.govukSummaryListKey(4) -> English.address,
        Selectors.button -> English.confirmAnswers,
      ))

      "have a link to change business name" in {
        doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change identifications" in {
        doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testDraftId, NormalMode).url
      }

      "have a link to change Vat Number" in {
        doc.getElementById("changeConsigneeExportVat").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Address" in {
        doc.getElementById("changeConsigneeAddress").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' for Eori'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] =
        dataRequest(FakeRequest(), emptyUserAnswers
          .set(ConsigneeAddressPage, testUserAddress)
          .set(ConsigneeBusinessNamePage, testBusinessName)
          .set(ConsigneeExcisePage, testErn)
          .set(ConsigneeExportInformationPage, Set(EoriNumber))
          .set(ConsigneeExportEoriPage, testEori)
          .set(DestinationTypePage, UkTaxWarehouse.GB)
        )

      lazy val list: list = app.injector.instanceOf[list]

      implicit val doc: Document = Jsoup.parse(view(
        controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testDraftId),
        testErn,
        testDraftId,
        SummaryList(Seq(
          ConsigneeBusinessNameSummary.row(true),
          ConsigneeExportInformationSummary(list).row(),
          ConsigneeExportEoriSummary.row(true),
          ConsigneeAddressSummary.row(true)
        ).flatten)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.caption,
        Selectors.govukSummaryListKey(1) -> English.traderName,
        Selectors.govukSummaryListKey(2) -> English.identificationProvided,
        Selectors.govukSummaryListKey(3) -> English.eori,
        Selectors.govukSummaryListKey(4) -> English.address,
        Selectors.button -> English.confirmAnswers,
      ))

      "have a link to change business name" in {
        doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change identifications" in {
        doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testDraftId, NormalMode).url
      }

      "have a link to change Eori Number" in {
        doc.getElementById("changeConsigneeExportEori").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testDraftId, CheckMode).url
      }

      "have a link to change Address" in {
        doc.getElementById("changeConsigneeAddress").attr("href") mustBe
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
      }
    }
  }
}
