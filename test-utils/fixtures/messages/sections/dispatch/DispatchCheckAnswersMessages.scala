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

object DispatchCheckAnswersMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Check your answers"
    val title = titleHelper(heading, Some(SectionMessages.English.dispatchSubHeading))
    val traderNameLabel = "Trader name"
    val traderNameChangeHidden = "Trader name"
    val addressLabel: String = "Place of dispatch details"
    val ern: String = "Excise ID (ERN)"
    val addressChangeHidden: String = "place of dispatch details"
    val dispatchWarehouseInvalidOrMissingOnSeedError = "The excise ID for the tax warehouse of dispatch is not valid"
    val dispatchWarehouseInvalidError = "The excise ID for the tax warehouse of dispatch is not valid"
    val dispatchWarehouseConsignorDoesNotManageWarehouseError = "Excise ID entered for the tax warehouse of dispatch is not linked with the consignor"
  }

  object English extends ViewMessages with BaseEnglish
}
