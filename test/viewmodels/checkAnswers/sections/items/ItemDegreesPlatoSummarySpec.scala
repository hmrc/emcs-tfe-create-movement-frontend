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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.items.ItemDegreesPlatoMessages
import fixtures.messages.sections.items.ItemDegreesPlatoMessages.ViewMessages
import models.requests.DataRequest
import models.sections.items.ItemDegreesPlatoModel
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemDegreesPlatoPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._
import views.html.components.tag

class ItemDegreesPlatoSummarySpec extends SpecBase with Matchers with MovementSubmissionFailureFixtures {

  val summary: ItemDegreesPlatoSummary = app.injector.instanceOf[ItemDegreesPlatoSummary]
  lazy val tag: tag = app.injector.instanceOf[tag]

  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  private def summaryRow(value: String, hasUpdateNeededTag: Boolean = false)
                        (implicit messagesForLanguage: ViewMessages, messages: Messages): SummaryListRow = {
    SummaryListRowViewModel(
      key = Key(Text(messagesForLanguage.cyaLabel)),
      value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
        Some(Html(value)),
        if (hasUpdateNeededTag) Some(tag("taskListStatus.updateNeeded", "orange", "float-none govuk-!-margin-left-1")) else None
      ).flatten))),
      actions = Seq(ActionItemViewModel(
        href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
        content = Text(messagesForLanguage.change),
        id = s"changeItemDegreesPlatoAmount${testIndex1.displayIndex}"
      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
    )
  }

  "ItemDegreesPlatoSummarySummary" - {

    Seq(ItemDegreesPlatoMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgsForLanguage: ViewMessages = messagesForLanguage

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "if provided" - {
          "and hasDegreesPlato is true" - {
            "must return a row with their answer if degreesPlato is provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              summary.row(idx = testIndex1) mustBe Some(summaryRow(s"1.59${messagesForLanguage.cyaSuffix}"))
            }
            "must return a row with default answer if degreesPlato is not provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = None))
            ) {
              summary.row(idx = testIndex1) mustBe Some(summaryRow(messagesForLanguage.no))
            }
            "must return a row (with the update needed tag when a submission failure exists)" in new Test(
              emptyUserAnswers
                .copy(submissionFailures = Seq(itemDegreesPlatoFailure(1)))
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              summary.row(idx = testIndex1) mustBe Some(summaryRow(
                value = s"1.59${messagesForLanguage.cyaSuffix}",
                hasUpdateNeededTag = true
              ))
            }
          }

          "and hasDegreesPlato is false" - {
            "must return a row with default answer even if degreesPlato is provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = false, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              summary.row(idx = testIndex1) mustBe Some(summaryRow(messagesForLanguage.no))
            }
            "must return a row with default answer if degreesPlato is not provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = false, degreesPlato = None))
            ) {
              summary.row(idx = testIndex1) mustBe Some(summaryRow(messagesForLanguage.no))
            }
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            summary.row(idx = testIndex1) mustBe None
          }
        }
      }
    }
  }
}
