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
import fixtures.messages.sections.items.ItemCommercialDescriptionMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemCommercialDescriptionPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemCommercialDescriptionSummarySpec extends SpecBase with Matchers {
  "ItemCommercialDescriptionSummary" - {

    Seq(ItemCommercialDescriptionMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemCommercialDescriptionSummary.row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.checkYourAnswersLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemCommercialDescriptionController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeItemCommercialDescription1"
                  ).withVisuallyHiddenText(messagesForLanguage.changehidden)
                )

              )
            )
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemCommercialDescriptionPage(testIndex1), "value"))

            ItemCommercialDescriptionSummary.row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("value")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemCommercialDescriptionController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeItemCommercialDescription1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            )
          }
        }
      }
    }
  }
}
