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

package fixtures.messages.sections.guarantor

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object GuarantorRequiredMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Movement guarantee"
    val title = titleHelper(heading)
    val isRequiredHeading = "Your movement requires a guarantee"
    val isRequiredTitle = titleHelper(isRequiredHeading)
    val p1 = "Generally, excise goods moving within the UK (between UK warehouses or before export), or from Northern Ireland to the EU, must be covered by financial security in the form of a movement guarantee."
    val isRequiredP1 = "This movement must be covered by financial security in the form of a movement guarantee."
    val isRequiredNIToEUP1 = "This movement must be covered by financial security in the form of a movement guarantee. The only type of movement that is allowed to take place without a guarantee from Northern Ireland to the EU is the movement of energy products using fixed transport installations."
    val isRequiredEnterDetails = "Enter guarantor details"
    val p2 = "It is the consignor’s responsibility to make sure that a valid movement guarantee is in place before the goods are dispatched. Movement guarantees are valid if lodged with HMRC and one of the businesses named on the guarantee has a suitable connection to that movement. For example, they are the consignor or the owner of the goods."
    val p3Link = s"Read more about financial security for movements of excise goods here $opensInNewTab."
    val inset = "The guarantor is liable for the whole of the movement until the movement has correctly ended. This means the goods reaching their stated destination or, for exports, leaving the UK. However, if the guarantor of a UK to UK movement is the consignee and the destination is changed to an export, a new guarantor is needed."
    val h2 = "Movements that do not require a guarantee"
    val p4 = "The following movement types do not usually require a guarantee:"
    val bullet1 = "movements of energy products from Northern Ireland to the EU using fixed transport installations"
    val bullet2 = "duty-suspended movements of goods with Excise Product Codes B000, W200 or W300, from UK alcohol production premises to other UK tax or excise warehouses. However, if a change of destination is submitted and the new destination is outside of the UK, a guarantor is required"
    val question = "Is a guarantee required for this movement?"
    val cyaLabel: String = "Guarantor required"
    val cyaChangeHidden: String = "if a guarantor is required for this movement"
    val errorRequired = "Select yes if a guarantor is required for this movement"

  }

  object English extends ViewMessages with BaseEnglish
}
