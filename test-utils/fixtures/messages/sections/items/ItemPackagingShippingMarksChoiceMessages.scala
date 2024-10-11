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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemPackagingShippingMarksChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Shipping mark"

    val title: String = titleHelper(heading)

    def p1(quantity: String) = s"Packaging 1 for item 1 is Bag: $quantity."

    val p2 = "Shipping marks are an optional way of identifying any packages that have more than one type of item packed inside."

    val detailsSummary = "Using shipping marks to show that this packaging contains mixed items (optional)"

    val detailsP1 = "EMCS allows you to optionally record that mixed items are packed together inside the same package. However, this is not necessary if each item already has individual packaging entered. For example, if you pack both wine and beer on a single pallet, you can choose to specify the same pallet for both those items following the instructions below, or you can record the bottles for the wine and the cans for the beer separately."
    val detailsP2 = "If you choose to record two or more mixed items against the same package, you need to make sure each item uses the same shipping mark and that the overall quantity of packages in the movement is correct. To do this:"

    val detailsBullet1 = "for the first item packed inside the package, enter the package quantity and a shipping mark (to identify the package)"
    val detailsBullet2 = "when entering the packaging for any different items packed together inside the same package, choose the same packaging type and select the same shipping mark, but enter a packaging quantity of 0"

    val legend = "Do you want to enter a shipping mark for this packaging?"

    val yesSelectExistingShippingMark = "Yes - select an existing shipping mark"

    def noHint(i: Int) = s"Item $i must have a quantity of more than 0 if you are not using shipping marks to identify this package"

    val warningText = s"$warning If you select that you don’t want a shipping mark, the shipping mark currently assigned to this packaging will be removed. Any other packages in this movement using the same shipping mark with a packaging quantity of 0 will therefore be invalid, and will also be removed."

    val cyaYesExistingShippingMarkSelected = "Yes - existing shipping mark selected"
    val cyaLabel = "Shipping mark choice"
    val cyaChangeHidden = "Shipping mark choice"
  }

  object English extends ViewMessages with BaseEnglish
}
