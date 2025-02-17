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

package fixtures.messages.sections.transportUnit

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object TransportUnitAddToListMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "You have given information for 1 transport unit"
    val title = titleHelper(heading, Some(SectionMessages.English.transportUnitSubHeading))

    val headingMultiple = "You have given information for 2 transport units"
    val titleMultiple = titleHelper(headingMultiple, Some(SectionMessages.English.transportUnitSubHeading))

    val question = "Do you need to add another transport unit?"
    val yesOption = "Yes"
    val noOptionSingular = "No, this is the only transport unit for this movement"
    val noOptionPlural = "No, these are all the transport units"
    val laterOption = "I will add more transport units later"
    val errorMessage = "Select yes if you need to add another transport unit"
    val errorMessageHelper: String => String = s"Error: " + _


    val transportUnit1 = "Transport unit 1"
    val transportUnit2 = "Transport unit 2"
    val removeLink1WithHiddenText    = "Remove (Transport unit 1)"
    val removeLink1WithHiddenTextIncomplete    = "Remove ( Transport unit 1 Incomplete )"
    val removeLink2WithHiddenText    = "Remove (Transport unit 2)"
    val editLink1WithHiddenText = "Continue editing (Transport unit 1)"
    val editLink1WithHiddenTextIncomplete = "Continue editing ( Transport unit 1 Incomplete )"

    val transportSealTypeMessages: BaseMessages
    val transportSealChoiceMessages: BaseMessages
    val transportUnitGiveMoreInformationChoiceMessages: BaseMessages
    val transportUnitGiveMoreInformationMessages: BaseMessages
    val transportUnitIdentityMessages: BaseMessages
    val transportUnitTypeMessages: BaseMessages

    val finalCyaCardTitle = "Transport units"
    val finalCyaKey: Int => String = "Transport unit " + _
    def finalCyaValue(transportUnit: String, id: Option[String]): String = s"$transportUnit${id.fold("")(i => s" ($i)")}"

    val tu1MustBeFixedGuarantorVariant = "Transport unit 1 must be fixed transport installations because there is no guarantor for this movement. Only movements of energy products by fixed transport installations can have no guarantor."
    val tu1MustBeFixed = "Transport unit 1 must be fixed transport installations because the journey type selected is fixed transport installations."
    val addAGuarantor = "A transport unit has been added that is not fixed transport installations, so you are now required to add a guarantor."
  }

  object English extends ViewMessages with BaseEnglish {
    override val transportSealTypeMessages: BaseMessages = TransportSealTypeMessages.English
    override val transportSealChoiceMessages: BaseMessages = TransportSealChoiceMessages.English
    override val transportUnitGiveMoreInformationChoiceMessages: BaseMessages = TransportUnitGiveMoreInformationChoiceMessages.English
    override val transportUnitGiveMoreInformationMessages: BaseMessages = TransportUnitGiveMoreInformationMessages.English
    override val transportUnitIdentityMessages: BaseMessages = TransportUnitIdentityMessages.English
    override val transportUnitTypeMessages: BaseMessages = TransportUnitTypeMessages.English
  }
}
