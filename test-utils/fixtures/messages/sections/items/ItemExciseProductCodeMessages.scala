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

    val cyaLabel = "Excise Product Code"
    val cyaChangeHidden = "Excise Product Code"

    val defaultSelectOption = "Choose Excise Product Code"
    val beerSelectOption = "B000: Beer"

    val label = "Select the Excise Product Code (EPC)"
    val hint = "Start typing the EPC code to see suggestions"
    val paragraph = "You will be asked to give information and packaging details about this item. You can return to this screen to add more items later."

    val warningText = s"$warning Changing the EPC code removes any information completed for this item and you will be asked to enter new item information"

    val insetTextGBNoGuarantor = "<strong>A UK to UK movement with no guarantor must select EPC codes B000, W200 or W300.</strong> If you choose to select a different EPC code you are required to add a guarantor."
    val insetTextEuNoGuarantor = "<strong>NI to EU movements with no guarantor must select energy products.</strong> If you choose to select a different EPC code you are required to add a guarantor."
    val insetTextUnknownDestination = "<strong>Movements to an unknown destination must select energy products.</strong> If you choose to select a different EPC code this movement will fail submission."

    val itemExciseProductCodeConsignorNotApprovedToSendError = "The excise product code for this item must be one that the consignor is approved to send"
    val itemExciseProductCodeConsigneeNotApprovedToReceiveError = "The excise product code for this item must be one that the consignee is approved to receive"
    val itemExciseProductCodeDestinationNotApprovedToReceiveError = "The excise product code for this item must be one that the tax warehouse of destination is approved to receive"
    val itemExciseProductCodeDispatchPlaceNotAllowed = "The tax warehouse of dispatch entered is not allowed to dispatch items with this excise product code"
  }

  object English extends ViewMessages with BaseEnglish

}
