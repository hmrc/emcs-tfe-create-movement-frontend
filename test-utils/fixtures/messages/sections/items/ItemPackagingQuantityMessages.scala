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

    val summary = "Help with recording quantity of packaging which contains mixed items (optional)"

    val detailsP1 = "EMCS allows you to optionally record that mixed items are packed together inside the same package. However, this is not necessary if each item already has individual packaging entered. For example, if you pack both wine and beer on a single pallet, you can choose to specify the same pallet for both those items following the instructions below, or you can record the bottles for the wine and the cans for the beer separately."

    val detailsP2 = "If you choose to record two or more mixed items against the same package, you need to make sure the total quantity of packages in your movement is correct. To do this:"

    val detailsBullet1 = "for the first item packed inside the package, enter the package quantity and a shipping mark (to identify the package)"

    val detailsBullet2 = "when entering the packaging for any different items packed together inside the same package, choose the same packaging type and the same shipping mark, but enter a packaging quantity of 0"

    val cyaLabel = "Packaging quantity"
    val cyaVisuallyHidden = "packaging quantity"
  }

  object English extends ViewMessages with BaseEnglish

}
