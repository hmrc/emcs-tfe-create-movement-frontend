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

object ItemWineOriginMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading: String = s"What was the wine’s country of origin?"
    val title: String = titleHelper(heading, Some(SectionMessages.English.itemsSubHeading))
    val hint: String = "Search by country name or abbreviation"

    val defaultSelectOption = "Choose country"
    val auSelectOption = "Australia (AU)"

    val cyaLabel = "Wine country of origin"
    val cyaChangeHidden = "wine country of origin"

    val errorRequired = "Select the wine’s country of origin"
  }

  object English extends ViewMessages with BaseEnglish

}

