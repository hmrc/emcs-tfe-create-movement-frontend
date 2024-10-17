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
import models.Index

object ItemPackagingEnterShippingMarksMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(packagingIndex: Index): String = s"Shipping mark for packaging ${packagingIndex.displayIndex}"
    def title(packagingIndex: Index): String = titleHelper(heading(packagingIndex), Some(SectionMessages.English.itemsSubHeading))

    def p1(packagingIndex: Index, itemIndex: Index, packagingDescription: String, packagingQuantity: String) =
      s"Packaging ${packagingIndex.displayIndex} for item ${itemIndex.displayIndex} is $packagingDescription: $packagingQuantity."

    def label(packagingIndex: Index) = s"Enter a shipping mark for packaging ${packagingIndex.displayIndex}"

    val hint = "Enter a description of a mark or number that can be seen on the outside of the packaging."

    val link = "This packaging has no shipping marks"

    def errorShippingMarkNotUnique(itemIndex: Index) = s"Enter a shipping mark value that has not been used before, or if you wish to select an existing shipping mark to show that item ${itemIndex.displayIndex} is packed together with another item then item ${itemIndex.displayIndex}’s packaging quantity must be changed to 0"

    val cyaLabel = "Shipping mark"
    val cyaChangeHidden = "shipping mark"
  }

  object English extends ViewMessages with BaseEnglish

}
