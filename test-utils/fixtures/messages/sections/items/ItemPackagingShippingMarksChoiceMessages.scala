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

    val detailsSummary = "Using shipping marks to show that this packaging contains mixed items"

    val detailsP1 = "For example, if you pack both wine and beer on a single pallet, you can choose to specify that pallet for both items. To make sure the total quantity of packages in your movement is correct:"

    val detailsBullet1 = "enter the packaging details, packaging quantity and a shipping mark for the first item packed inside this packaging"
    val detailsBullet2 = "enter the same packaging details but with a packaging quantity of 0 for any additional items packed together inside this packaging"
    val detailsBullet3 = "select the same shipping mark for all items packed together inside"

    val legend = "Do you want to enter a shipping mark for this packaging?"

    val yesSelectExistingShippingMark = "Yes - select an existing shipping mark"

    def noHint(i: Int) = s"Item $i must have a quantity of more than 0 if you are not using shipping marks to identify this package"

    val cyaLabel = "Shipping mark choice"
    val cyaChangeHidden = "Shipping mark choice"
  }

  object English extends ViewMessages with BaseEnglish
}
