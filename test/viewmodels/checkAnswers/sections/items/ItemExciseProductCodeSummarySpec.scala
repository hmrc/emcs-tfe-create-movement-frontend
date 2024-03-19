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
import fixtures.messages.sections.items.ItemExciseProductCodeMessages
import fixtures.messages.sections.items.ItemExciseProductCodeMessages.ViewMessages
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import models.ReviewMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemExciseProductCodePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils._
import viewmodels.govuk.summarylist._
import views.html.components.{p, tag}

class ItemExciseProductCodeSummarySpec extends SpecBase
  with Matchers
  with ItemFixtures
  with MovementSubmissionFailureFixtures {

  lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val tag: tag = app.injector.instanceOf[tag]

  private def summaryRow(hasUpdateNeededTag: Boolean = false)(implicit messagesForLanguage: ViewMessages, messages: Messages): SummaryListRow = {
    SummaryListRowViewModel(
      key = Key(Text(messagesForLanguage.cyaLabel)),
      value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
        Some(p()(Html(testCommodityCodeWine.exciseProductCode))),
        Some(p()(Html(testCommodityCodeWine.exciseProductCodeDescription))),
        if (hasUpdateNeededTag) Some(tag("taskListStatus.updateNeeded", "orange", "float-none govuk-!-margin-left-1")) else None
      ).flatten))),
      actions = Seq(ActionItemViewModel(
        href = controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, ReviewMode).url,
        content = Text(messagesForLanguage.change),
        id = s"changeItemExciseProductCode${testIndex1.displayIndex}"
      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
    )
  }

  "ItemExciseProductCodeSummary" - {

    Seq(ItemExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        implicit val msgsForLanguage: ViewMessages = messagesForLanguage

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testCommodityCodeWine.exciseProductCode))

            itemExciseProductCodeSummary.row(testIndex1, testCommodityCodeWine, ReviewMode) mustBe summaryRow()
          }

          "must return a row with the update needed tag when a submission failure exists" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testCommodityCodeWine.exciseProductCode).copy(
                submissionFailures = Seq(
                  itemExciseProductCodeFailure(ItemExciseProductCodeConsignorNotApprovedToSendError(testIndex1, isForAddToList = false), itemIndex = 1)
                )
              ))

            itemExciseProductCodeSummary.row(idx = testIndex1, testCommodityCodeWine, ReviewMode) mustBe summaryRow(hasUpdateNeededTag = true)
          }
        }
      }
    }
  }
}
