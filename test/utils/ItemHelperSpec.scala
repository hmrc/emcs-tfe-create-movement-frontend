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

package utils

import base.SpecBase
import fixtures.ItemFixtures
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}

class ItemHelperSpec extends SpecBase with ItemFixtures {

  ".isWine" - {

    "return true" - {

      "when the EPC and commodity code is defined and" - {

        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

        "the item is Wine and the commodity code is 22060010" in {
          ItemHelper.isWine(testIndex1)(userAnswers.set(ItemCommodityCodePage(testIndex1), "22060010")) mustBe true
        }

        "the item is Wine and the commodity code starts with '2204' but is not '22043096' or '22043098'" in {
          ItemHelper.isWine(testIndex1)(userAnswers.set(ItemCommodityCodePage(testIndex1), "22041000")) mustBe true
        }
      }
    }

    "return false" - {

      "the EPC is not defined" in {

        ItemHelper.isWine(testIndex1)(emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), "22060010")) mustBe false
      }

      "the commodity code is not defined" in {

        ItemHelper.isWine(testIndex1)(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)) mustBe false
      }

      "the commodity code is 22043096" in {
        ItemHelper.isWine(testIndex1)(emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), "22043096")
        ) mustBe false
      }

      "the commodity code is 22060010 and the item is not Wine" in {

        ItemHelper.isWine(testIndex1)(emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), "22060010")
        ) mustBe false
      }

      "the commodity code is 22043098" in {

        ItemHelper.isWine(testIndex1)(emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), "22043098")
        ) mustBe false
      }

      "doesn't start with 2204" in {
        ItemHelper.isWine(testIndex1)(emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), "22031000")
        ) mustBe false
      }
    }
  }
}
