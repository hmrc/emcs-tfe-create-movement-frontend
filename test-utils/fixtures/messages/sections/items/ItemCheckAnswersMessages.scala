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

object ItemCheckAnswersMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val heading = "Check your answers"

    val title = titleHelper(heading)

    def subheading(idx: Index): String = s"Item ${idx.displayIndex}"

    def cardTitleItemDetails(itemIndex: Index) = s"Details for item ${itemIndex.displayIndex}"
    def cardTitleQuantity(itemIndex: Index) = s"Quantity for item ${itemIndex.displayIndex}"
    def cardTitleWineDetails(itemIndex: Index) = s"Wine details for item ${itemIndex.displayIndex}"
    def cardTitlePackagingType(itemIndex: Index) = s"Package type for item ${itemIndex.displayIndex}"
    def cardTitleIndividualPackaging(itemIndex: Index, packagingIndex: Index) = s"Packaging ${packagingIndex.displayIndex} for item ${itemIndex.displayIndex}"

    def packagingKey(idx: Index): String = s"Packaging type ${idx.displayIndex}"
    def packagingValue(quantity: String, packagingType: String): String = s"${quantity}x $packagingType"

    val notificationBannerContentForQuantity: String = "The item quantity is over the approved limit for the Temporary Registered Consignee"
    val notificationBannerContentForDegreesPlato: String = "Invalid degree plato for beer"

    val addMorePackaging: String = "Add more packaging"
  }

  object English extends ViewMessages with BaseEnglish

}
