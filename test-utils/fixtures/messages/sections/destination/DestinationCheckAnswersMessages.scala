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

object DestinationCheckAnswersMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Check your answers"
    val title = titleHelper(heading)
    val traderNameLabel = "Trader name"
    val traderNameChangeHidden = "Trader name"
    val sameAsConsignee: String = "Destination details same as consignee"
    val details: String = "Place of destination details"

    val placeOfDestinationExciseIdInvalidError = "The excise ID for the tax warehouse of destination is not valid"
    val placeOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError = "The excise ID for the tax warehouse of destination must be linked to the consignee"
    val placeOfDestinationExciseIdForTaxWarehouseInvalidError = "The excise ID for the tax warehouse of destination is not valid"
  }

  object English extends ViewMessages with BaseEnglish
}
