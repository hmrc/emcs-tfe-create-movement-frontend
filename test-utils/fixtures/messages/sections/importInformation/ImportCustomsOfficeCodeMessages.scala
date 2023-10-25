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

import fixtures.messages.{BaseEnglish, BaseMessages, BaseWelsh, i18n}
import models.{NorthernIrelandRegisteredConsignor, UserType}

object ImportCustomsOfficeCodeMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    def heading(userType: UserType): String =
      if (userType == NorthernIrelandRegisteredConsignor) {
        "Enter the customs office code for the place where the goods originally entered the EU"
      } else {
        "Enter the customs office code for the place where the goods originally entered the UK"
      }

    def title(userType: UserType): String = titleHelper(heading(userType))

    def paragraph(userType: UserType): String =
      if (userType == NorthernIrelandRegisteredConsignor) {
        "This code is also known as a customs office reference number. Search for UK customs office codes here (opens in new tab) or search for EU customs office codes here (opens in new tab)."
      } else {
        "This code is also known as a customs office reference number. Search for UK customs office codes here (opens in new tab)."
      }
  }

  object English extends ViewMessages with BaseEnglish

  object Welsh extends ViewMessages with BaseWelsh {
    override def paragraph(userType: UserType): String =
      if (userType == NorthernIrelandRegisteredConsignor) {
        "This code is also known as a customs office reference number. Search for UK customs office codes here (yn agor tab newydd) or search for EU customs office codes here (yn agor tab newydd)."
      } else {
        "This code is also known as a customs office reference number. Search for UK customs office codes here (yn agor tab newydd)."
      }
  }
}
