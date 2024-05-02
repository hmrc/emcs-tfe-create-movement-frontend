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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemDesignationOfOriginMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val heading: String = "Select a statement about the designation of origin of item 1"
    val title: String = titleHelper(heading)

    val headingS200: String = "Select statements about the designation of origin and labelling of item 1"
    val titleS200: String = titleHelper(headingS200)

    val geographicalIndicationHint: String = "For example, Champagne has a Protected Designation of Origin (PDO) status and Calvados wine has a Protected Geographical Indication (PGI) protected wine name."
    val spiritMarketingAndLabellingHint: String = "Spirits must meet certain rules in order to be marketed in relation to their maturation age, protected geographical names or spirit category, such as ‘Irish Whiskey’ or ‘dry gin’."

    val pdoRadio = "The product has a Protected Designation of Origin (PDO)"
    val pdoInput = "Enter the name and register number of the PDO (optional)"
    val pgiRadio = "The product has a Protected Geographical Indication (PGI)"
    val pgiInput = "Enter the name and register number of the PGI (optional)"
    val noGiRadio = "I don’t want to provide a statement about the designation of origin"

    val s200YesRadio = "It is hereby certified that the product described is marketed and labelled in compliance with Regulation (EU) 2019/787"
    val s200NoRadio = "I don’t want to provide a statement about the marketing and labelling of the spirit"

    val designationOfOriginLegendS200 = "Statement of designation of origin"
    val spiritMarketingAndLabellingLegend = "Statement of spirit marketing and labelling"

    val cyaLabel: String = "Statement of designation of origin"
    val cyaLabelS200: String = "Statements about the designation of origin and labelling of the product"

    val cyaChangeHidden: String = "Statement of designation of origin"
    val cyaChangeHiddenS200: String = "Statements about the designation of origin and labelling of the product"

  }

  object English extends ViewMessages with BaseEnglish

}
