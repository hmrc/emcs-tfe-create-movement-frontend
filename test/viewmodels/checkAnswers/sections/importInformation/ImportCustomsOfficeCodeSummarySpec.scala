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

package viewmodels.checkAnswers.sections.importInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.importInformation.ImportCustomsOfficeCodeMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper

class ImportCustomsOfficeCodeSummarySpec extends SpecBase with Matchers with MovementSubmissionFailureFixtures {

  lazy val tagHelper = app.injector.instanceOf[TagHelper]
  lazy val importCustomsOfficeCodeSummary = app.injector.instanceOf[ImportCustomsOfficeCodeSummary]

  "ImportCustomsOfficeCodeSummary" - {

    Seq(ImportCustomsOfficeCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        def expectedSummaryListRow(showActionLinks: Boolean = true, has704Error: Boolean = false) =
          SummaryListRowViewModel(
            key = Key(Text(messagesForLanguage.cyaLabel)),
            value = Value(HtmlContent(HtmlFormat.fill(Seq(
              Some(Html("AB123456")),
              Option.when(has704Error)(tagHelper.updateNeededTag())
            ).flatten))),
            actions = if (!showActionLinks) Seq() else Seq(
              ActionItemViewModel(
                content = Text(messagesForLanguage.change),
                href = controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, CheckMode).url,
                id = "changeImportCustomsOfficeCode"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
            )
          )

        "when there's no answer" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            importCustomsOfficeCodeSummary.row(showActionLinks = true) mustBe None
          }
        }

        "when there's an answer" - {

          "when the show action link boolean is true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456"))

              importCustomsOfficeCodeSummary.row(showActionLinks = true) mustBe
                Some(expectedSummaryListRow())
            }
          }

          "when the show action link boolean is false" - {

            "must output the expected row without action links" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456"))

              importCustomsOfficeCodeSummary.row(showActionLinks = false) mustBe
                Some(expectedSummaryListRow(showActionLinks = false))
            }
          }

          "when a 704 error exists for this answer that is not fixed" - {

            "must output the expected row with the update needed tag" in {

              implicit lazy val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456").copy(
                  submissionFailures = Seq(importCustomsOfficeCodeFailure)
                )
              )

              importCustomsOfficeCodeSummary.row(showActionLinks = true) mustBe
                Some(expectedSummaryListRow(has704Error = true))
            }
          }

          "when a 704 error exists for this answer BUT the issue has been fixed" - {

            "must output the expected row without the update needed tag" in {

              implicit lazy val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456").copy(
                  submissionFailures = Seq(importCustomsOfficeCodeFailure.copy(hasBeenFixed = true))
                )
              )

              importCustomsOfficeCodeSummary.row(showActionLinks = true) mustBe
                Some(expectedSummaryListRow())
            }
          }
        }
      }
    }
  }
}
