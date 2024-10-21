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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.response.referenceData.CnCodeInformation

object ItemQuantityMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def title(goodsType: String, cnCode: CnCodeInformation): String = {
      cnCode.exciseProductCode match {
        case "T200" => titleHelper(headingT200, Some(SectionMessages.English.itemsSubHeading))
        case "T300" => titleHelper(headingT300, Some(SectionMessages.English.itemsSubHeading))
        case _ => titleHelper(heading(goodsType), Some(SectionMessages.English.itemsSubHeading))
      }
    }

    def heading(goodsType: String) = s"How much $goodsType are you moving?"
    val headingT200 = "Number of cigarettes you are moving"
    val headingT300 = "Number of cigars or cigarillos you are moving"

    val paragraphT200 = "The excise duty is worked out per 1000 cigarettes. You can work out what to enter by dividing the total quantity of cigarettes you are moving by 1000. This does not mean the number of packets."
    val paragraphT300 = "The excise duty is worked out per 1000 cigars or cigarillos. You can work out what to enter by dividing the total quantity of cigars or cigarillos you are moving by 1000. This does not mean the number of packets."

    val labelT200 = "How many thousands of cigarettes are you moving?"
    val labelT300 = "How many thousands of cigars or cigarillos are you moving?"

    def hint(unitOfMeasure: String) = s"Enter the total quantity of liquids in $unitOfMeasure."
    val hintT200 = "For example, enter 5 for 5000 cigarettes, or enter 1234.567 for 1,234,567 cigarettes."
    val hintT300 = "For example, enter 5 for 5000 cigars, or enter 1234.567 for 1,234,567 cigars."
    def hintLiquid(unitOfMeasure: String) = s"Enter the total quantity of liquids in $unitOfMeasure."

    val cyaLabel = "Quantity"
    val cyaChangeHidden = "item quantity"

    val quantitySubmissionFailure = "The item quantity is over the approved limit for the Temporary Registered Consignee"
  }

  object English extends ViewMessages with BaseEnglish

}
