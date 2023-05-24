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

package fixtures.messages

object MonthMessages {

  sealed trait ViewMessages { _: i18n =>
    val jan: String
    val feb: String
    val mar: String
    val apr: String
    val may: String
    val jun: String
    val jul: String
    val aug: String
    val sep: String
    val oct: String
    val nov: String
    val dec: String
    def month(i: Int): String = i match {
      case 1 => jan
      case 2 => feb
      case 3 => mar
      case 4 => apr
      case 5 => may
      case 6 => jun
      case 7 => jul
      case 8 => aug
      case 9 => sep
      case 10 => oct
      case 11 => nov
      case 12 => dec
    }
  }

  object English extends ViewMessages with BaseEnglish {
    override val jan: String = "January"
    override val feb: String = "February"
    override val mar: String = "March"
    override val apr: String = "April"
    override val may: String = "May"
    override val jun: String = "June"
    override val jul: String = "July"
    override val aug: String = "August"
    override val sep: String = "September"
    override val oct: String = "October"
    override val nov: String = "November"
    override val dec: String = "December"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val jan: String = "January"
    override val feb: String = "February"
    override val mar: String = "March"
    override val apr: String = "April"
    override val may: String = "May"
    override val jun: String = "June"
    override val jul: String = "July"
    override val aug: String = "August"
    override val sep: String = "September"
    override val oct: String = "October"
    override val nov: String = "November"
    override val dec: String = "December"
  }
}
