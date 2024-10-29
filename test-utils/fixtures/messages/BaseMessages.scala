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
  def titleHelper(heading: String, section: Option[String] = None) =
    s"$heading ${section.fold("")("- " + _ + " ")}- Create and manage excise goods movements with EMCS - GOV.UK"
  val opensInNewTab: String = "(opens in new tab)"

  val continue = "Continue"
  val confirmAnswers = "Confirm answers"
  val notProvided = "Not provided"
  val clearSelectedCode = "Clear selected code"
  val saveAndContinue = "Save and continue"
  val confirmAndContinue = "Confirm and continue"
  val returnToDraft = "Return to draft"
  val skipThisQuestion = "Skip this question for now"
  val skipQuestion = "Skip this question"
  val saveAndReturnToMovement = "Save and return to movement"
  val day: String = "Day"
  val month: String = "Month"
  val year: String = "Year"
  val yes: String = "Yes"
  val no: String = "No"
  val none: String = "None"
  val change: String = "Change"
  val remove: String = "Remove"
  val continueEditing: String = "Continue editing"
  val sectionNotComplete: String => String = section => s"$section section not complete"
  val incomplete: String = "Incomplete"
  val updateNeededTag: String = "Update needed"
  val updateNeeded = "Update needed"
  val important = "Important"
  val notificationBannerParagraph = "Some information needs updating"
  val notificationBannerParagraphForItems = "Some information needs updating"
  val warning: String = "Warning:"
  val or = "or"
}

trait BaseEnglish extends BaseMessages with SectionMessages.English with EN
object BaseEnglish extends BaseEnglish
