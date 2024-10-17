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

object ItemSelectPackagingMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(itemIndex: String): String = s"Packaging for item $itemIndex"
    def title(itemIndex: String): String = titleHelper(heading(itemIndex), Some(SectionMessages.English.itemsSubHeading))
    def label(itemIndex: String) = s"Select the packaging for item $itemIndex"
    def hint = "Start typing to see suggestions."
    def paragraph(itemIndex: String): String =
      s"Add at least one type of packaging for item $itemIndex that can be checked or counted easily. You can add more packaging to this item later."

    val cyaLabel = "Packaging type"
    val cyaChangeHidden = "packaging type"

    val defaultSelectOption = "Choose packaging type"
    val aerosolSelectOption = "Aerosol (AE)"
  }

  object English extends ViewMessages with BaseEnglish

}
