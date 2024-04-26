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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemPackagingQuantityMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(itemNumber: String) = s"How many of this type of package are you using to move item $itemNumber?"
    def title(itemNumber: String): String = titleHelper(heading(itemNumber))

    def hint(packagingNumber: Int, itemNumber: Int, packageType: String) = s"Packaging $packagingNumber for item $itemNumber is: $packageType."

    val summary = "Help with packaging quantity if it contains other items"

    val detailsP1 = "EMCS allows you to show items packed together inside the same packaging. For example, if you pack both wine and beer on a single pallet, you can choose to specify that pallet for both items. To make sure the total quantity of packages in your movement is correct:"

    val detailsBullet1 = "enter the packaging details, packaging quantity and a shipping mark for the first item packed inside this packaging"

    val detailsBullet2 = "enter the same packaging details but with a packaging quantity of 0 for any additional items packed together inside this packaging"

    val detailsBullet3 = "select the same shipping mark for all items packed together inside"

    val detailsP2 = "However, you don’t need to record the shared packaging in EMCS if each item already has countable packaging entered, such as bottles, cans or cases."

    val cyaLabel = "Quantity"
    val cyaVisuallyHidden = "packaging quantity"
  }

  object English extends ViewMessages with BaseEnglish

}
