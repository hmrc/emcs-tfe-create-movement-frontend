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

object DestinationWarehouseExciseMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val title = titleHelper("What is the excise registration number (ERN) for the destination tax warehouse?")
    val heading: String = "What is the excise registration number (ERN) for the destination tax warehouse?"
    val cyaLabel: String = "Warehouse excise ID"
    val cyaChangeHidden: String = "Warehouse excise ID"
    val errorRequired: String = "Enter the excise registration number (ERN)"
    val errorInvalidCharacters: String = "ERN must not include < and > and : and ;"
    val errorLength: String = "VAT registration number must be 16 characters or less"
    val text: String = "This number contains 13 alpha-numeric characters starting with GB and can be found on your approval letter."
  }
  object English extends ViewMessages with BaseEnglish
}
