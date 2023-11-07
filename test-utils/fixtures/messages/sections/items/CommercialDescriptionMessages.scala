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

object CommercialDescriptionMessages {
  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Enter a commercial description for the $goodsType"
    def title(goodsType: String): String = titleHelper(heading(goodsType))
    val checkYourAnswersLabel = "Commercial Description"
    val errorRequired = "Enter a commercial description of the goods"
    val paragraph = "This will be used to help identify the goods."
    val errorlength = "Commercial description must be 350 characters or less"
    val  changehidden = "changeCommercialDescription"
    val hintb = "For example: 5 x cases of 12 x 330ml cans session IPA."
    val hintw = "For example: 50 x 750 ml bottles of merlot red wine, Bordeaux, France, vintage year 2021."
    val hintf = "For example: 2 x boxes of 6 x 750ml bottles elderflower wine."
    val hinti = "Include any information that will help to identify the goods, including the number of the lowest level of packaging used, such as bottles."
    val  hints = "For example: 20 x 1 litre bottles of London dry gin."
    val  hintt = "For example: 500 x 30g packets of hand rolling tobacco."
    val hinte = "Include any information that will help to identify the goods."
    val detailsBeer1 = "Include any information that will help to identify the goods, such as:"
    val detailsBeer2 = "- the number of the lowest level of packaging used, such as cans, bottles or kegs"
    val  detailsBeer3 = "- if transporting multipacks or cases, state both the number of multipacks or cases, and the number of individual units. For example: 10 x cases of 12 x 300ml bottles of lager beer"
    val detailsWine1 = "Include any information that will help to identify the goods, such as:"
    val  detailsWine2 = "- the number and volume of the lowest level of packaging used, such as bottles or barrels"
    val detailsWine3 = "- the region and country of origin"
    val detailsWine4 = "- the grape variety, such as ‘merlot’"
    val detailsWine5 = "- any production method terms such as ‘oak-aged’ or ‘extra-dry’"
    val detailsWine6 = "- vintage year"
    val  detailsFermentedBeverage1 = "Include any information that will help to identify the goods, such as:"
    val detailsFermentedBeverage2 = "- the number of the lowest level of packaging used, such as cans, bottles or kegs"
    val detailsFermentedBeverage3 = "- if transporting multipacks or cases, state both the number of multipacks or cases, and the number of individual units. For example: 10 x cases of 12 x 500ml bottles of cider"
    val detailsEthylAlcohol1 = "Include any information that will help to identify the goods, such as:"
    val detailsEthylAlcohol2 = "- the number and volume of the lowest level of packaging used, such as cans, bottles or drums"
    val detailsEthylAlcohol3 = "if transporting multipacks state both the number of multipacks and number of individual units. For example: 5 x boxes of 6 x 35cl bottles of London dry gin"
    val  detailsEthylAlcohol4 = "- the spirit name (such as rum or vodka)"
    val  detailsEthylAlcohol5 = "- if the spirit drink is dry, blended or mixed"
    val detailsTobacco1 = "Include any information that will help to identify the goods, such as:"
    val detailsTobacco2 = "- the number of the lowest level of packaging used, such as individual cigarette packets or cigar boxes"
    val detailsTobacco3 = "- if transporting multipacks state both the number of multipacks and number of individual units. For example: 200 x multipacks of 5 x 20 cigarettes"
    val detailsTobacco4 = "- if transporting hand rolling or pipe tobacco state the number and weight of individual packets."
    val summary = "Help with commercial description"
    val errorXss = "Document reference must not contain < and > and : and ;"
  }

  object English extends ViewMessages with BaseEnglish


}

