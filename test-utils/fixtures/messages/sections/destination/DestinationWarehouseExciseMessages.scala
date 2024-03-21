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

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading: String = "What is the excise ID of the tax warehouse of destination?"
    val title = titleHelper(heading)
    val cyaLabel: String = "Warehouse excise ID"
    val cyaChangeHidden: String = "Warehouse excise ID"
    val errorRequired: String = "Enter the excise ID of the tax warehouse of destination"
    val errorLength: String = "Excise ID of the tax warehouse must be 16 characters or less"
    val errorInvalidCharacters: String = "Excise ID of the tax warehouse must not include < and > and : and ;"
    val text: String = "This is sometimes called an excise registration number (ERN), starting with two letters identifying the member state of the destination warehouse. For example, GB00123456789."

    val placeOfDestinationExciseIdInvalidError = "The excise ID for the tax warehouse of destination is not valid"
    val placeOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError = "The excise ID for the tax warehouse of destination must be linked to the consignee"
    val placeOfDestinationExciseIdForTaxWarehouseInvalidError = "The excise ID for the tax warehouse of destination is not valid"

  }

  object English extends ViewMessages with BaseEnglish
}
