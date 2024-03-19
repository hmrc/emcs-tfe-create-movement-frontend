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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object CheckYourAnswersConsigneeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Check your answers"
    val title: String = titleHelper(heading)
    val caption: String = "Consignee information"
    val ern: String = "Excise registration number (ERN)"
    val traderName: String = "Trader name"
    val address: String = "Address"
    val eori: String = "EORI number"
    val vat: String = "VAT registration number"
    val identificationProvided: String = "Identification provided"
    val exempt: String = "Exempted organisation details"

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
