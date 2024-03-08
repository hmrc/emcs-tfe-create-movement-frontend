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

package views.sections.importInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.importInformation.CheckYourAnswersImportMessages
import models.{CheckMode, UserAnswers}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import viewmodels.checkAnswers.sections.importInformation.ImportCustomsOfficeCodeSummary
import views.html.sections.importInformation.CheckYourAnswersImportView
import views.{BaseSelectors, ViewBehaviours}

class CheckYourAnswersImportViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  class Fixture(lang: Lang, userAnswers: UserAnswers = emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456")) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

    lazy val view = app.injector.instanceOf[CheckYourAnswersImportView]
    lazy val importCustomsOfficeCodeSummary = app.injector.instanceOf[ImportCustomsOfficeCodeSummary]

    implicit val doc: Document = Jsoup.parse(view(
      testErn,
      testDraftId,
      SummaryList(Seq(
        importCustomsOfficeCodeSummary.row(true)
      ).flatten)
    ).toString())
  }

  object Selectors extends BaseSelectors{
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"
    val fixLink = "#fix-import-customs-office-code"
  }

  "CheckYourAnswersImportView" - {

    Seq(CheckYourAnswersImportMessages.English).foreach { messagesForLanguage =>

      "when a 704 error does NOT exist" - {

        s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - new Fixture(messagesForLanguage.lang) {

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.importInformationSection,
            Selectors.govukSummaryListKey(1) -> messagesForLanguage.customsOfficeCode,
            Selectors.button -> messagesForLanguage.confirmAnswers
          ))

          behave like pageWithElementsNotPresent(Seq(
            Selectors.notificationBannerTitle,
            Selectors.notificationBannerContent
          ))

          "have a link to change customs office code" in {
            doc.getElementById("changeImportCustomsOfficeCode").attr("href") mustBe
              controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, CheckMode).url
          }
        }
      }

      "when a 704 error exists for the page" - {

        val userAnswers = emptyUserAnswers
          .set(ImportCustomsOfficeCodePage, "AB123456")
          .copy(submissionFailures = Seq(importCustomsOfficeCodeFailure))

        "must render with the Update needed banner" - new Fixture(messagesForLanguage.lang, userAnswers) {

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.notificationBannerTitle -> messagesForLanguage.notificationBannerTitle,
            Selectors.notificationBannerContent -> messagesForLanguage.importCustomsOffice704Error
          ))
        }

        "must have link to the ImportCustomsOffice page" in new Fixture(messagesForLanguage.lang, userAnswers) {
          doc.select(Selectors.fixLink).attr("href") mustBe
            controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, CheckMode).url
        }
      }
    }
  }
}
