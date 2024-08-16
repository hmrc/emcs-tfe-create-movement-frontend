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

package fixtures.messages.sections.destination

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object DestinationConsigneeDetailsMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Is the place of destination the same as the consignee details?"
    val title = titleHelper(heading)
    val errorRequired = "Select yes if the place of destination and consignee details are the same"

    val cyaLabel: String = "Destination details same as consignee"

    val cyaChangeHidden: String = "if destination details are same as consignee"

    val errorMessageHelper: String => String = s"Error: " + _
  }

  object English extends ViewMessages with BaseEnglish
}
