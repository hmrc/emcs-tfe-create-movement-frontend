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
import fixtures.BaseFixtures
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem

class ItemExciseProductCodeHelperSpec extends SpecBase with BaseFixtures {

  ".constructSelectItemsForEPCs" - {
    "should return a list of select items (no pre-selection when there isn't an existing answer)" in {
      val result = ItemExciseProductCodeHelper.constructSelectItemsForEPCs(Seq(beerExciseProductCode, wineExciseProductCode))
      result mustBe Seq(
        SelectItem(value = Some("B000"), text = "B000: Beer", selected = false),
        SelectItem(value = Some("W200"), text = "W200: Still wine and still fermented beverages other than wine and beer", selected = false)
      )
    }

    "should return a list of select items (pre-selected when there is an existing answer)" in {
      val result = ItemExciseProductCodeHelper.constructSelectItemsForEPCs(Seq(beerExciseProductCode, wineExciseProductCode), Some("W200"))
      result mustBe Seq(
        SelectItem(value = Some("B000"), text = "B000: Beer", selected = false),
        SelectItem(value = Some("W200"), text = "W200: Still wine and still fermented beverages other than wine and beer", selected = true)
      )
    }
  }

}
