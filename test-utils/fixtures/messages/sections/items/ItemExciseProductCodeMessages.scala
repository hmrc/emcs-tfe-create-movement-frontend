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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import models.Index

object ItemExciseProductCodeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(idx: Index): String = s"Choose the Excise Product Code (EPC) for item ${idx.displayIndex}"

    def title(idx: Index): String = titleHelper(heading(idx))

    val cyaLabel = "Excise product code"
    val cyaChangeHidden = "Excise product code"

    val defaultSelectOption = "Choose Excise Product Code"
    val beerSelectOption = "B000: Beer"

    val label = "Excise Product Code"
    val paragraph = "You will be asked to give information and packaging details about this item. You can return to this screen to add more items later."

    val warningText = s"$warning Changing the EPC code removes any information completed for this item and you will be asked to enter new item information"

    val itemExciseProductCodeConsignorNotApprovedToSendError = "The excise product code for this item must be one that the consignor is approved to send"
    val itemExciseProductCodeConsigneeNotApprovedToReceiveError = "The excise product code for this item must be one that the consignee is approved to receive"
    val itemExciseProductCodeDestinationNotApprovedToReceiveError = "The excise product code for this item must be one that the tax warehouse of destination is approved to receive"
    val itemExciseProductCodeDispatchPlaceNotAllowed = "The tax warehouse of dispatch entered is not allowed to dispatch items with this excise product code"
  }

  object English extends ViewMessages with BaseEnglish

}
