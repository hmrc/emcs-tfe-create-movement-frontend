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
import fixtures.messages.sections.items.ItemDegreesPlatoMessages
import models.CheckMode
import models.sections.items.ItemDegreesPlatoModel
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemDegreesPlatoPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemDegreesPlatoSummarySpec extends SpecBase with Matchers {

  "ItemDegreesPlatoSummarySummary" - {

    lazy val app = applicationBuilder().build()

    Seq(ItemDegreesPlatoMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemDegreesPlatoSummary.row(testIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          "when the answer is Yes with a value" - {

            "must output the expected rows" in {
              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(true, Some(5))))

              ItemDegreesPlatoSummary.row(testIndex1) mustBe Some(Seq(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaRadioLabel,
                  value = Value(Text(messagesForLanguage.yes)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeItemDegreesPlatoRadio1"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                ),
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaAmountLabel,
                  value = Value(Text(s"5 ${messagesForLanguage.degreesPlatoSuffix}")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeItemDegreesPlatoAmount1"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              ))
            }
          }

          "when the answer is No" - {

            "must output the expected rows" in {
              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(false, None)))

              ItemDegreesPlatoSummary.row(testIndex1) mustBe Some(Seq(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaRadioLabel,
                  value = Value(Text(messagesForLanguage.no)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeItemDegreesPlatoRadio1"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              ))
            }
          }
        }
      }
    }
  }
}
