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

package viewmodels.checkAnswers.sections.exportInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.exportInformation.ExportCustomsOfficeMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.exportInformation.ExportCustomsOfficePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import utils.ExportCustomsOfficeNumberError
import viewmodels.govuk.summarylist._
import views.html.components.tag

class ExportCustomsOfficeSummarySpec extends SpecBase with Matchers with MovementSubmissionFailureFixtures {

  lazy val summary: ExportCustomsOfficeSummary = app.injector.instanceOf[ExportCustomsOfficeSummary]
  lazy val tag: tag = app.injector.instanceOf[tag]

  "ExportCustomsOfficeSummary" - {
    Seq(ExportCustomsOfficeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        def expectedRow(hasUpdateNeededTag: Boolean = false): Option[SummaryListRow] = {
          Some(SummaryListRowViewModel(
            key = Key(Text(messagesForLanguage.cyaLabel)),
            value = Value(HtmlContent(HtmlFormat.fill(Seq(
              Some(Html(testExportCustomsOffice)),
              if(hasUpdateNeededTag) Some(tag("taskListStatus.updateNeeded", "orange", "float-none govuk-!-margin-left-1")) else None
            ).flatten))),
            actions = Seq(
              ActionItemViewModel(
                content = Text(messagesForLanguage.change),
                href = controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(testErn, testDraftId, CheckMode).url,
                id = "changeExportCustomsOffice"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
            )
          ))
        }

        "when there's no answer" - {

          "must output the Not Provided" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            summary.row(showActionLinks = true) mustBe None
          }
        }

        "when there's an answer" - {

          "when the show action link boolean is true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ExportCustomsOfficePage, testExportCustomsOffice))

              summary.row(showActionLinks = true) mustBe expectedRow()
            }
          }
        }

        "and there is a 704 error" - {

          "must render an 'Update Needed' tag against the LRN - when the error has not been fixed" in {

            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers
                .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false)))
                .set(ExportCustomsOfficePage, testExportCustomsOffice))

            summary.row(showActionLinks = true) mustBe expectedRow(hasUpdateNeededTag = true)
          }

          "must not render an 'Update Needed' tag against the LRN - when the error has been fixed" in {

            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers
                .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = true)))
                .set(ExportCustomsOfficePage, testExportCustomsOffice))

            summary.row(showActionLinks = true) mustBe expectedRow()
          }
        }
      }
    }
  }
}
