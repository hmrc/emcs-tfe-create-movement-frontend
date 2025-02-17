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

package fixtures.messages.sections.importInformation

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.{NorthernIrelandRegisteredConsignor, UserType}

object ImportCustomsOfficeCodeMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    def heading(userType: UserType): String =
      if (userType == NorthernIrelandRegisteredConsignor) {
        "Customs office code for the place where the goods originally entered the EU or UK"
      } else {
        "Customs office code for the place where the goods originally entered the UK"
      }

    def title(userType: UserType): String = titleHelper(heading(userType), Some(SectionMessages.English.importInformationSubHeading))

    def paragraph(userType: UserType): String =
      if (userType == NorthernIrelandRegisteredConsignor) {
        "This code is also known as a customs office reference number. Search for UK customs office codes (opens in new tab) or search for EU customs office codes (opens in new tab)."
      } else {
        "This code is also known as a customs office reference number. Search for UK customs office codes (opens in new tab)."
      }

    val label = "Enter the customs office code"
    val hint = "The code starts with 2 letters representing the member state, followed by 6 numbers or mixed letters and numbers. For example, GB000060."

    val importCustomsOffice704Error = "The import customs office code you have entered is not valid"

    val cyaLabel: String = "Customs office code"
    val cyaChangeHidden: String = "customs office code"
  }

  object English extends ViewMessages with BaseEnglish
}
