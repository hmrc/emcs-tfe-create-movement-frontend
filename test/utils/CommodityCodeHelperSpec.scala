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

class CommodityCodeHelperSpec extends SpecBase {

  ".isWineCommodityCode" - {

    "return true" - {

      "the commodity code is 22060010" in {
        CommodityCodeHelper.isWineCommodityCode("22060010") mustBe true
      }

      "the commodity code starts with '2204' but is not '22043096' or '22043098'" in {
        CommodityCodeHelper.isWineCommodityCode("22041000") mustBe true
      }
    }

    "return false" - {

      "the commodity code is 22043096" in {
        CommodityCodeHelper.isWineCommodityCode("22043096") mustBe false
      }

      "the commodity code is 22043098" in {
        CommodityCodeHelper.isWineCommodityCode("22043098") mustBe false
      }

      "doesn't start with 2204" in {
        CommodityCodeHelper.isWineCommodityCode("22031000") mustBe false
      }
    }
  }
}
