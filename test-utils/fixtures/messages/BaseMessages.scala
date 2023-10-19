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


trait BaseMessages { _: i18n =>
  def titleHelper(heading: String) = s"$heading - Excise Movement and Control System - GOV.UK"
  val opensInNewTab: String = "(opens in new tab)"
  val movementInformationSection: String = "Movement information"
  val consigneeInformationSection = "Consignee information"
  val transportArrangerSection: String = "Transport arranger"
  val transportUnitsSection: String = "Transport units"
  val dispatchSection: String = "Place of dispatch information"
  val exportInformationSection: String = "Export information"
  val destinationSection: String = "Place of destination information"
  def lrnSubheading(lrn: String): String = s"Create movement for $lrn"
  val continue = "Continue"
  val confirmAnswers = "Confirm answers"
  val notProvided = "Not provided"
  val saveAndContinue = "Save and continue"
  val returnToDraft = "Return to draft"
  val skipThisQuestion = "Skip this question for now"
  val saveAndReturnToMovement = "Save and return to movement"
  val day: String = "Day"
  val month: String = "Month"
  val year: String = "Year"
  val yes: String = "Yes"
  val no: String = "No"
  val change: String = "Change"
  val remove: String = "Remove"
  val sectionNotComplete: String => String = section => s"$section section not complete"
}

trait BaseEnglish extends BaseMessages with EN
object BaseEnglish extends BaseEnglish

trait BaseWelsh extends BaseMessages with CY
object BaseWelsh extends BaseWelsh
