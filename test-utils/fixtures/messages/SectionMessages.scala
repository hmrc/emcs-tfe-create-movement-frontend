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

object SectionMessages {

  trait ViewMessages extends BaseMessages {
    _: i18n =>

    val journeyTypeSubHeading = "Journey type"
    val consigneeSubHeading = "Consignee information"
    val consignorSubHeading = "Consignor information"
    val transportArrangerSubHeading = "Transport arranger"
    val firstTransporterSubHeading = "First transporter"
    val transportUnitSubHeading = "Transport units"
    val dispatchSubHeading = "Place of dispatch information"
    val destinationSubHeading = "Place of destination information"
    val guarantorSubHeading = "Guarantor"
    val movementInformationSubHeading = "Movement information"
    val exportInformationSubHeading = "Export information"
    val importInformationSubHeading = "Import information"
    val documentsSubHeading = "Documents"
    val itemsSubHeading = "Item information"
    val sadSubHeading = "Single Administrative Document"
    val draftMovementSubHeading = "Draft movement"

    val hiddenSectionContent: String = "This section is"

    val movementInformationSection: String = s"$hiddenSectionContent $movementInformationSubHeading"
    val consigneeInformationSection = s"$hiddenSectionContent $consigneeSubHeading"
    val consignorInformationSection = s"$hiddenSectionContent $consignorSubHeading"
    val transportArrangerSection: String = s"$hiddenSectionContent $transportArrangerSubHeading"
    val dispatchSection: String = s"$hiddenSectionContent $dispatchSubHeading"
    val exportInformationSection: String = s"$hiddenSectionContent $exportInformationSubHeading"
    val importInformationSection: String = s"$hiddenSectionContent $importInformationSubHeading"
    val destinationSection: String = s"$hiddenSectionContent $destinationSubHeading"
    val sadSection: String = s"$hiddenSectionContent $sadSubHeading"
    val firstTransporterSection: String = s"$hiddenSectionContent $firstTransporterSubHeading"
    val documentsSection: String = s"$hiddenSectionContent $documentsSubHeading"
    val itemSection: String = s"$hiddenSectionContent $itemsSubHeading"
    val draftMovementSection: String = s"$hiddenSectionContent $draftMovementSubHeading"
    val transportUnitsSection: String = s"$hiddenSectionContent $transportUnitSubHeading"
    val journeyTypeSection: String = s"$hiddenSectionContent $journeyTypeSubHeading"
    val guarantorSection: String = s"$hiddenSectionContent $guarantorSubHeading"
  }

  trait English extends ViewMessages with EN
  object English extends English
}
