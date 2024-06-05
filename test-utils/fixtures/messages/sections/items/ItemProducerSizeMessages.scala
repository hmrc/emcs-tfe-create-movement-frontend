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

object ItemProducerSizeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    def headingGoodsType(goodsType: String, start: String, end: String) =
      s"What was the producer’s total production of $goodsType from 1 February $start to 31 January $end?"
    def headingPureAlcohol(start: String, end: String) =
      s"What was the producer’s total pure alcohol production from 1 February $start to 31 January $end?"
    def titleGoodsType(goodsType: String, start: String, end: String): String = titleHelper(headingGoodsType(goodsType, start: String, end: String))
    def titlePureAlcohol(start: String, end: String): String = titleHelper(headingPureAlcohol(start: String, end: String))

    val p = "This information should be provided when claiming Small Producer Relief on Alcohol Duty."
    val p2GoodsType = "You should enter the total production in hectolitres of finished product."
    val p2PureAlcohol = "You should enter the total production in hectolitres of pure alcohol."
    val inputSuffix = "hl"
    val labelGoodsType = "Annual production of finished product"
    val labelPureAlcohol = "Annual production of pure alcohol"
    val hint = "Amount in hectolitres should be a whole number."
    val link = "I am unable to provide this information"
    val errorRequired = "Enter the producer’s total production"
    val errorWholeNumber = "Amount must not include a decimal place"
    val errorNonNumeric = "Amount must only contain numbers"
    val errorOutOfRange = "Enter an amount between 1 and 15 digits"
    val cyaLabelForPureAlcohol = "Producer’s production of pure alcohol"
    val cyaChangeHiddenForPureAlcohol = "producer’s production of pure alcohol"
    val cyaLabelForFinishedProduct = "Producer’s production of finished product"
    val cyaChangeHiddenForFinishedProduct = "producer’s production of finished product"
  }

  object English extends ViewMessages with BaseEnglish

}
