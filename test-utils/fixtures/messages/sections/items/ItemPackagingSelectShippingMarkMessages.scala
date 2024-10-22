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

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.Index

object ItemPackagingSelectShippingMarkMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def heading(i: Index): String = s"Shipping mark for packaging ${i.displayIndex}"

    def title(i: Index): String = titleHelper(heading(i), Some(SectionMessages.English.itemsSubHeading))

    def p1(packagingNumber: String, itemNumber: String, packagingType: String, packagingQuantity: String) = s"Packaging $packagingNumber for item $itemNumber is $packagingType: $packagingQuantity."

    def p2(i: Index): String = s"Select a description of a mark or number that can be seen on the outside of the packaging." +
      s" By selecting this shipping mark you are confirming that item ${i.displayIndex}" +
      s" is packed together inside the same package as any other items using this shipping mark."

    val defaultValue = "Select a shipping mark"
  }

  object English extends ViewMessages with BaseEnglish
}
