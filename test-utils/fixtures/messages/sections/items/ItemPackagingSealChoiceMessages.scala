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

object ItemPackagingSealChoiceMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val heading: String = "Is there a commercial seal on this packaging?"
    val title: String = titleHelper(heading)
    val p1 = "This is a seal that prevents items being removed from or added to this packaging. You will have the opportunity to give details of any seals on the transport later."
    def p2(packagingIndex: String, itemIndex: String,packagingDescription: String, packagingQuantity: String) = s"Packaging $packagingIndex for item $itemIndex is $packagingDescription: $packagingQuantity."
    val cyaLabel: String = "Commercial seal"
    val cyaChangeHidden: String = "commercial seal"
  }

  object English extends ViewMessages with BaseEnglish
}
