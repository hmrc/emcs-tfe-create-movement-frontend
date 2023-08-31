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

import play.api.i18n.Lang


sealed trait BaseMessages { _: i18n =>
  def title(heading: String): String
  val opensInNewTab: String
  def lrnSubheading(lrn: String): String
  val lang: Lang
  val saveAndContinue: String
  val continue: String
  val saveAndReturnToMovement: String
  val day: String
  val month: String
  val year: String
  val yes: String
  val no: String
  val change: String
  val remove: String
}

trait BaseEnglish extends BaseMessages with EN {
  override def title(heading: String) = s"$heading - Excise Movement and Control System - GOV.UK"
  override val opensInNewTab: String = "(opens in new tab)"
  override def lrnSubheading(lrn: String): String = s"Create movement for $lrn"
  override val saveAndContinue = "Save and continue"
  override val continue = "Continue"
  override val saveAndReturnToMovement = "Save and return to movement"
  override val day: String = "Day"
  override val month: String = "Month"
  override val year: String = "Year"
  override val yes: String = "Yes"
  override val no: String = "No"
  override val change: String = "Change"
  override val remove: String = "Remove"
}
object BaseEnglish extends BaseEnglish

trait BaseWelsh extends BaseMessages with CY {
  override def title(heading: String) = s"$heading - Excise Movement and Control System - GOV.UK"
  override val opensInNewTab: String = "(opens in new tab)"
  override def lrnSubheading(lrn: String): String = s"Create movement for $lrn"
  override val saveAndContinue = "Save and continue"
  override val continue = "Continue"
  override val saveAndReturnToMovement = "Save and return to movement"
  override val day: String = "Day"
  override val month: String = "Month"
  override val year: String = "Year"
  override val yes: String = "Yes"
  override val no: String = "No"
  override val change: String = "Change"
  override val remove: String = "Remove"
}
object BaseWelsh extends BaseWelsh
