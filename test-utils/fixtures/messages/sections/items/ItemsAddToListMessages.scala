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

object ItemsAddToListMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    def heading(count: Int): String = count match {
      case 1 => "You have given information for 1 item"
      case _ => s"You have given information for $count items"
    }

    def title(count: Int): String = titleHelper(heading(count), Some(SectionMessages.English.itemsSubHeading))

    def itemCardTitle(idx: Index) = s"Item ${idx.displayIndex}"

    def removeItem(idx: Index): String = s"Remove ( ${itemCardTitle(idx)} )"
    def removeItemIncomplete(idx: Index): String = s"Remove ( ${itemCardTitle(idx)} Incomplete )"

    def changeItem(idx: Index): String = s"Change ( ${itemCardTitle(idx)} )"

    def editItem(idx: Index): String = s"Continue editing ( ${itemCardTitle(idx)} )"
    def editItemIncomplete(idx: Index): String = s"Continue editing ( ${itemCardTitle(idx)} Incomplete )"

    val h2 = "Do you need to add another item?"
    val no1 = "No, this is the only item in this movement"
    val no2 = "No, these are the only items in this movement"
    val moreLater = "I will add more items later"

    val errorRequired = "Select yes if you need to add another item"

    val packagesCyaLabel: String = "Packaging"
    val packagesCyaValueShippingMarkSummary: String = "View full shipping mark"
    def packagesCyaValue(quantity: String, description: String): String = s"${quantity}x $description"
    def packagesCyaValueShippingMark(quantity: String, description: String, shippingMark: String): String =
      s"${quantity}x $description (shipping mark: $shippingMark)"
    def packagesCyaValueShippingMarkTruncated(quantity: String, description: String, shippingMark: String): String =
      s"${quantity}x $description (shipping mark: ${shippingMark.take(30)}...)"

    val notificationBannerContentForQuantity: Int => String = index => s"Item $index quantity"

    val finalCyaCardTitle = "Items"
    def finalCyaKey(quantity: String, unit: String, goodsType: String) = s"$quantity $unit of $goodsType"
  }

  object English extends ViewMessages with BaseEnglish
}
