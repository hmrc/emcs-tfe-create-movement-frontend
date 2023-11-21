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

package models.response.referenceData

import base.SpecBase
import play.api.libs.json.Json

class CnCodeInformationSpec extends SpecBase {
  "reads" - {
    "must read JSON to a model" in {
      Json.obj(
        "cnCode" -> testCnCodeTobacco,
        "cnCodeDescription" -> "Cigarettes containing tobacco / other",
        "exciseProductCode" -> testEpcTobacco,
        "exciseProductCodeDescription" -> "Cigarettes",
        "unitOfMeasureCode" -> 1
      ).as[CnCodeInformation] mustBe testCommodityCodeTobacco
    }

    "must replace &lsquo; with '" in {
      Json.obj(
        "cnCode" -> testCnCodeTobacco,
        "cnCodeDescription" -> "This is a &lsquo;test'",
        "exciseProductCode" -> testEpcTobacco,
        "exciseProductCodeDescription" -> "Cigarettes",
        "unitOfMeasureCode" -> 1
      ).as[CnCodeInformation] mustBe testCommodityCodeTobacco.copy(cnCodeDescription = "This is a 'test'")
    }
  }
}
