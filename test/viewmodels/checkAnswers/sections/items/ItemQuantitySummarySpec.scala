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
import fixtures.messages.UnitOfMeasureMessages
import fixtures.messages.sections.items.ItemQuantityMessages
import models.CheckMode
import models.UnitOfMeasure.Kilograms
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemQuantityPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemQuantitySummarySpec extends SpecBase with Matchers {

  "ItemQuantitySummary" - {

    Seq(ItemQuantityMessages.English -> UnitOfMeasureMessages.English).foreach { case (messagesForLanguage, unitOfMeasure) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemQuantitySummary.row(testIndex1, Kilograms) mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(1)))

            ItemQuantitySummary.row(testIndex1, Kilograms) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(s"1 ${unitOfMeasure.kilogramsShort}")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeItemQuantity1"
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
