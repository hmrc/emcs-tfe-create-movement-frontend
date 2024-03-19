/*
 * Copyright 2024 HM Revenue & Customs
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

package views.sections.exportInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.exportInformation.ExportInformationCheckAnswersViewMessages
import models.CheckMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterNamePage, FirstTransporterVatPage}
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import utils.ExportCustomsOfficeNumberError
import viewmodels.checkAnswers.sections.exportInformation.ExportCustomsOfficeSummary
import views.html.sections.exportInformation.ExportInformationCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class ExportInformationCheckAnswersViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  val summaryRow = app.injector.instanceOf[ExportCustomsOfficeSummary]

  class Fixture(lang: Lang, withErrorMessage: Boolean = false) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
      emptyUserAnswers
        .copy(submissionFailures = if(withErrorMessage) Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false)) else Seq.empty)
        .set(FirstTransporterNamePage, "Transporter name")
        .set(FirstTransporterVatPage, "GB123456789")
        .set(FirstTransporterAddressPage, testUserAddress)
    )

    lazy val view = app.injector.instanceOf[ExportInformationCheckAnswersView]

    implicit val doc: Document = Jsoup.parse(view(
      SummaryList(Seq(
        summaryRow.row(showActionLinks = true)
      ).flatten),
      testOnwardRoute
    ).toString())
  }

  object Selectors extends BaseSelectors {
    val exportCustomOfficeError = "#export-customs-office-number-error"

    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"
  }

  "ExportInformationCheckAnswersView" - {

    Seq(ExportInformationCheckAnswersViewMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - new Fixture(messagesForLanguage.lang) {

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.exportInformationSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

      }

      "when there is a 704 error" - new Fixture(messagesForLanguage.lang, withErrorMessage = true) {
        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.exportInformationSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
          Selectors.notificationBannerContent -> messagesForLanguage.exportCustomsOfficeSubmissionFailure,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))(doc)

        "link to the export customs office page" in {
          doc.select(Selectors.exportCustomOfficeError).attr("href") mustBe controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(testErn, testDraftId, CheckMode).url
        }
      }
    }
  }
}
