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

package fixtures.messages.sections.dispatch

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object DispatchWarehouseExciseMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Excise ID"
    val heading2 = "What is the excise ID of the tax warehouse of dispatch?"
    val placeOfDispatchHeading = "This section is Place of dispatch information"
    val h2 = "For movements of vaping products from the EU to Northern Ireland"
    val p1 = "Enter the excise ID in this format: EU Country code followed by VPD12345678."
    val p2 = "For example, FRVPD12345678"
    val link = "Find EU country codes (opens in new tab)."
    val p = p1 + " " + p2 + " " + link

    val title = titleHelper(heading, Some(SectionMessages.English.dispatchSubHeading))
    val hintText = "This is sometimes called an excise registration number (ERN), starting with GB or XI. For example, GB00123456789."
    val cyaLabel = "Excise ID (ERN)"
    val cyaChangeHidden = "Excise ID (ERN) of the tax warehouse of dispatch"

    val dispatchWarehouseInvalidOrMissingOnSeedError = "The excise ID for the tax warehouse of dispatch is not valid"
    val dispatchWarehouseInvalidError = "The excise ID for the tax warehouse of dispatch is not valid"
    val dispatchWarehouseConsignorDoesNotManageWarehouseError = "Excise ID entered for the tax warehouse of dispatch is not linked with the consignor"
  }

  object English extends ViewMessages with BaseEnglish
}
