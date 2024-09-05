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
import fixtures.messages.UnitOfMeasureMessages
import fixtures.messages.sections.items.ItemQuantityMessages
import fixtures.messages.sections.items.ItemQuantityMessages.ViewMessages
import models.requests.DataRequest
import models.{CheckMode, UnitOfMeasure, UserAnswers}
import pages.sections.items.ItemQuantityPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class ItemQuantitySummarySpec extends SpecBase with MovementSubmissionFailureFixtures {

  val summary: ItemQuantitySummary = app.injector.instanceOf[ItemQuantitySummary]

  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  private def summaryRow(implicit messagesForLanguage: ViewMessages, messages: Messages): Option[SummaryListRow] = {
    Some(SummaryListRowViewModel(
      key = Key(Text(messagesForLanguage.cyaLabel)),
      value = ValueViewModel(HtmlContent(Html(s"1.23 ${UnitOfMeasure.Kilograms.toShortFormatMessage()}"))),
      actions = Seq(ActionItemViewModel(
        href = controllers.sections.items.routes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
        content = Text(messagesForLanguage.change),
        id = s"changeItemQuantity${testIndex1.displayIndex}"
      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
    ))
  }

  "ItemQuantitySummary" - {

    Seq(ItemQuantityMessages.English -> UnitOfMeasureMessages.English).foreach { case (messagesForLanguage, unitOfMeasure) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        implicit val msgsForLanguage: ViewMessages = messagesForLanguage

        "if provided" - {

          "must return a row" in new Test(emptyUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(1.23))) {
            summary.row(idx = testIndex1, unitOfMeasure = UnitOfMeasure.Kilograms) mustBe summaryRow
          }
        }

        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            summary.row(idx = testIndex1, unitOfMeasure = UnitOfMeasure.Kilograms) mustBe None
          }
        }
      }
    }
  }
}
