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

package viewmodels.helpers

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items._
import models.UserAnswers
import models.requests.DataRequest
import models.sections.items._
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.checkAnswers.sections.items.{ItemCommodityCodeSummary, ItemExciseProductCodeSummary}

class ItemCheckAnswersHelperSpec extends SpecBase with ItemFixtures {
  val messagesForLanguage: ItemCheckAnswersMessages.English.type = ItemCheckAnswersMessages.English

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  class Test(val userAnswers: UserAnswers) {
    lazy implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, testErn)
    lazy implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
    lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
    lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]

    lazy val helper = new ItemCheckAnswersHelper(itemExciseProductCodeSummary, itemCommodityCodeSummary)
  }

  "ItemCheckAnswersHelper" - {
    "constructItemDetailsCard" - {
      "must return rows" in new Test(baseUserAnswers) {
        helper.constructItemDetailsCard(testIndex1, testCommodityCodeWine) must not be empty
      }
    }

    "constructQuantityCard" - {
      "must return all rows" in new Test(
        baseUserAnswers
          .set(ItemQuantityPage(testIndex1), BigDecimal(1.23))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
      ) {
        helper.constructQuantityCard(testIndex1, testCommodityCodeWine).length mustBe 3
      }
    }
  }
}
