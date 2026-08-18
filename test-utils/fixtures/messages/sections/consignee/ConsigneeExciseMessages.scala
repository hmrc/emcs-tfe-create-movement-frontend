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

package fixtures.messages.sections.consignee

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.sections.info.movementScenario.DestinationType
import models.sections.info.movementScenario.DestinationType.{TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}


object ConsigneeExciseMessages {
  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val h1 = "Excise Registration Number (ERN)"
    val heading = "What is the consignee’s excise registration number (ERN)?"
    val h2 = "For movements of vaping products from Northern Ireland to the EU"
    val p1 = "Enter the excise ID in this format: EU Country code followed by VPD12345678."
    val p2 = "For example, FRVPD12345678"
    val link = "Find EU country codes (opens in new tab)."
    val p = p1 + " " + p2 + " " + link
    val title: String = titleHelper(h1, Some(SectionMessages.English.consigneeSubHeading))
    val hint = "An ERN contains 13 characters, starting with 2 letters that represent the member state of the consignee, such as GBWK123456789. It can be found on your approval letter."

    val temporaryRegisteredConsigneeHeading = "What is the Temporary Registered Consignee’s authorisation reference?"
    val temporaryRegisteredConsigneeTitle: String = titleHelper(temporaryRegisteredConsigneeHeading, Some(SectionMessages.English.consigneeSubHeading))
    val temporaryRegisteredConsigneeHint = "This contains 13 characters, starting with 2 letters that represent the member state of the Temporary Registered Consignee. For example, XI12345678900. This is also known as a Temporary Consignment Authorisation (TCA) number."

    val temporaryCertifiedConsigneeHeading = "What is the Temporary Certified Consignee’s authorisation reference?"
    val temporaryCertifiedConsigneeTitle: String = titleHelper(temporaryCertifiedConsigneeHeading, Some(SectionMessages.English.consigneeSubHeading))
    val temporaryCertifiedConsigneeHint = "This contains 13 characters, starting with 2 letters that represent the member state of the Temporary Certified Consignee. For example, XI12345678900. This is also known as a Temporary Consignment Authorisation (TCA) number."

    def cyaLabel(destinationType: DestinationType): String = {
      destinationType match {
        case TemporaryRegisteredConsignee =>
          "Identification number for Temporary Registered Consignee"
        case TemporaryCertifiedConsignee =>
          "Identification number for Temporary Certified Consignee"
        case _ =>
          "Excise registration number (ERN)"
      }
    }

    def cyaChangeHidden(destinationType: DestinationType): String = {
      destinationType match {
        case TemporaryRegisteredConsignee =>
          "consignee identification number for Temporary Registered Consignee"
        case TemporaryCertifiedConsignee =>
          "consignee identification number for Temporary Certified Consignee"
        case _ =>
          "consignee excise registration number (ERN)"
      }
    }

    val invalidOrMissingConsignee = "The consignee Excise Registration Number is not valid for the destination type of this movement"
    val linkIsPending = "The temporary authorisation reference entered cannot be verified because the linked Temporary Certificate of Authority is pending"
    val linkIsAlreadyUsed = "The temporary authorisation reference entered cannot be verified because the linked Temporary Certificate of Authority has already been used"
    val linkIsWithdrawn = "The temporary authorisation reference entered cannot be verified because the linked Temporary Certificate of Authority has been withdrawn"
    val linkIsCancelled = "The temporary authorisation reference entered cannot be verified because the linked Temporary Certificate of Authority has been cancelled"
    val linkIsExpired = "The temporary authorisation reference entered cannot be verified because the linked Temporary Certificate of Authority has expired"
    val linkMissingOrInvalid = "The temporary authorisation reference entered is invalid because the consignee’s Temporary Certificate of Authority is not held on EMCS"
    val directDeliveryNotAllowed = "The consignee entered is not allowed to receive direct deliveries"
    val consignorNotAuthorised = "The consignor is not authorised to trade with the temporary registered consignee"
    val registeredConsignorToRegisteredConsignee = "A registered consignor cannot send goods to a registered consignee"
    val consigneeRoleInvalid = "The consignee Excise Registration Number is not valid for this type of movement"
  }

  object English extends ViewMessages with BaseEnglish
}
