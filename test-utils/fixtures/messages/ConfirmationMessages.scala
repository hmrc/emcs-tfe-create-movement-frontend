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

object ConfirmationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Draft movement submitted"
    val title: String = titleHelper(heading)
    val movementInformationHeader = "Movement information"
    val localReferenceNumber = "Local Reference Number"
    val dateOfSubmission = "Date of submission"
    val printText = "Print this screen to make a record of your submission."

    val whatHappensNextHeader = "What happens next"
    val p1 = "If the submission is successful you’ll get a 21 digit administrative reference code (ARC) sent to your messages inbox. This can take up to 15 minutes."
    val p2 = "Goods must not be dispatched before an ARC has been allocated."
    val p3 = "The ARC must be visible on either a printed electronic Administrative Document (eAD) or any other commercial document, and must travel with the goods."
    val p4 = "Once your goods have been delivered, you will receive a Report of Receipt from the consignee to let you know if the movement was satisfactory, or if there were any problems."

    val ifYouNeedToChange = "If you need to change or cancel the movement"
    val p5DutySuspended = "You can choose to cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, or the goods are in transit, you can choose to submit a change of destination or an explanation of a delay."
    val p5DutyPaid = "You can choose to cancel a movement up to the date and time recorded on the electronic administrative document (eAD). To cancel this movement, submit a change of destination and select to return the goods to the consignor's place of dispatch. If the date and time on the eAD has passed, or the goods are in transit, you can choose to submit an explanation of a delay or a change of destination with the same consignee."
    val p6DutySuspended = "Links to cancel, change the movement or explain a delay can be found in the movement overview."
    val p6DutyPaid = "Links to change the movement or explain a delay can be found in the movement overview."

    val ifUnsuccessful = "If your submission is unsuccessful"
    val p7 = "If unsuccessful, you’ll get an error message sent to your messages inbox."
    val p8 = "The message will tell you what needs to be corrected. You must correct and resubmit the movement until you get an ARC."
    val p9 = "If the error cannot be corrected, you must create a new movement."
    val p10 = "Contact the HMRC excise helpline if you need more help or information about excise duties."

    val returnToAccountLink = "Return to account home"
    val feedbackLink = "What did you think of this service? (opens in new tab) (takes 30 seconds)"

  }

  object English extends ViewMessages with BaseEnglish
}
