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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemPackagingShippingMarksChoiceMessages
import models.CheckMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemPackagingShippingMarksChoiceSummarySpec extends SpecBase with Matchers {

  "ItemPackagingShippingMarksChoiceSummary" - {

    Seq(ItemPackagingShippingMarksChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemPackagingShippingMarksChoiceSummary.row(testIndex1, testPackagingIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          val rowWithChangeLink: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabel,
            value = Value(Text(messagesForLanguage.yes)),
            actions =
            Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.items.routes.ItemPackagingShippingMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1,
                  testPackagingIndex1, CheckMode).url,
                id = "changeItemPackagingShippingMarksChoice1ForItem1"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
            )
          )

          val userAnsers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

            "must output the expected row" - {

              "when the answer is yes (quantity > 0)" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), true)
                    .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "blah")
                    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
                  )

                ItemPackagingShippingMarksChoiceSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowWithChangeLink.copy(value = Value(Text(messagesForLanguage.yes)))
                )
              }

              "when the answer is yes (quantity == 0)" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), true)
                    .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "blah")
                    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "0")
                  )

                ItemPackagingShippingMarksChoiceSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowWithChangeLink.copy(value = Value(Text(messagesForLanguage.cyaYesExistingShippingMarkSelected)))
                )
              }

              "when the answer is no" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
                  )

                ItemPackagingShippingMarksChoiceSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowWithChangeLink.copy(value = Value(Text(messagesForLanguage.no)))
                )
              }
            }
          }
        }
    }
  }
}
