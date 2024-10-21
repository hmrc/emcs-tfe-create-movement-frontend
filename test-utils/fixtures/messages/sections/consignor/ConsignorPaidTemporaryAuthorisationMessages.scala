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

package fixtures.messages.sections.consignor

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object ConsignorPaidTemporaryAuthorisationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter the consignor’s Paid Temporary Authorisation (PTA) code"
    val title = titleHelper(heading, Some(SectionMessages.English.consignorSubHeading))
    val hint = "The PTA contains 13 characters starting with XIPTA. It will be different to the Excise Registration Number you signed in to EMCS with."
    val errorMessageHelper: String => String = s"Error: " + _

    val errorRequired = "Enter the Paid Temporary Authorisation (PTA) code"
    val errorLength = "Paid Temporary Authorisation (PTA) must be 13 characters or less"
    val errorInvalid = "Paid Temporary Authorisation (PTA) must start with XIPTA followed by 8 numbers or mixed letters and numbers"

  }

  object English extends ViewMessages with BaseEnglish
}

