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
import models.Index

object ItemPackagingShippingMarksMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading: String = "What shipping marks are on this packaging?"
    val title: String = titleHelper(heading)

    val p1 = "Enter any marks or numbers on the packaging that will help to identify it. " +
      "Use the same shipping marks for any other product types contained within this package that are entered as a separate item."
    def p2(packagingType: String) = s"The packaging type is: $packagingType."

    val link = "This packaging has no shipping marks"

    def errorShippingMarkNotUnique(itemIndex: Index) = s"Enter a shipping mark value that has not been used before, or if you wish to select an existing shipping mark to show that item ${itemIndex.displayIndex} is packed together with another item then item ${itemIndex.displayIndex}’s packaging quantity must be changed to 0"

    val cyaLabel = "Shipping mark description"
    val cyaChangeHidden = "shipping mark description"
  }

  object English extends ViewMessages with BaseEnglish

}
