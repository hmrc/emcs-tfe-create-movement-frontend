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
import fixtures.messages.sections.items.ItemPackagingShippingMarksMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.items.{ItemExciseProductCodePage, ItemPackagingShippingMarksPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemPackagingShippingMarksSummarySpec extends SpecBase with Matchers {

  "ItemPackagingShippingMarksSummary" - {

    Seq(ItemPackagingShippingMarksMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemPackagingShippingMarksSummary.row(testIndex1, testPackagingIndex1) mustBe None
          }

        }

        "when there's an answer" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "B000")
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
            )


            ItemPackagingShippingMarksSummary.row(testIndex1, testPackagingIndex1) mustBe Some(SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text("answer")),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.items.routes.ItemPackagingShippingMarksController.onPageLoad(testErn, testDraftId, testIndex1,
                    testPackagingIndex1, CheckMode).url,
                  id = "changeItemPackagingShippingMarks1ForItem1"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            ))
          }
        }
      }
    }
  }
}
