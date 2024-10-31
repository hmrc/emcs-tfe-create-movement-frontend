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

import models.UserAddress
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import pages.QuestionPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.destination.DestinationAddressPage
import pages.sections.dispatch.DispatchAddressPage
import pages.sections.firstTransporter.FirstTransporterAddressPage
import pages.sections.guarantor.GuarantorAddressPage
import pages.sections.transportArranger.TransportArrangerAddressPage

object AddressMessages {

  trait ViewMessages extends BaseMessages {
    _: i18n =>

    val heading = (page: QuestionPage[UserAddress]) => page match {
      case ConsignorAddressPage => "Enter the consignor’s details"
      case _ => "Enter the consignee’s details"
    }

    def title(page: QuestionPage[UserAddress], subHeading: Option[String]): String = titleHelper(heading(page), subHeading)

    val transportArrangerAddressGoodsOwnerHeading = "Enter the goods owner’s details"
    val transportArrangerAddressGoodsOwnerTitle = titleHelper(transportArrangerAddressGoodsOwnerHeading, Some(SectionMessages.English.transportArrangerSubHeading))
    val transportArrangerAddressOtherHeading = "Enter the transporter’s details"
    val transportArrangerAddressOtherTitle = titleHelper(transportArrangerAddressOtherHeading, Some(SectionMessages.English.transportArrangerSubHeading))

    val firstTransporterAddressHeading = "Enter the first transporter’s details"
    val firstTransporterAddressTitle = titleHelper(firstTransporterAddressHeading, Some(SectionMessages.English.firstTransporterSubHeading))

    val destinationAddressHeading = "Enter the place of destination details"
    val destinationAddressTitle = titleHelper(destinationAddressHeading, Some(SectionMessages.English.destinationSubHeading))

    val dispatchAddressHeading = "Enter the place of dispatch details"
    val dispatchAddressTitle = titleHelper(dispatchAddressHeading, Some(SectionMessages.English.dispatchSubHeading))

    val dispatchAddressOptionalHeading = "Enter the place of dispatch details (optional)"
    val dispatchAddressOptionalTitle = titleHelper(dispatchAddressOptionalHeading, Some(SectionMessages.English.dispatchSubHeading))

    def guarantorAddressHeading(guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => "Enter the goods owner’s details"
      case Transporter => "Enter the transporter’s details"
      case _ => ""
    }

    def guarantorAddressTitle(guarantorArranger: GuarantorArranger): String = titleHelper(guarantorAddressHeading(guarantorArranger), Some(SectionMessages.English.guarantorSubHeading))

    val subheading = (page: QuestionPage[UserAddress]) => page match {
      case ConsignorAddressPage => SectionMessages.English.consignorSubHeading
      case TransportArrangerAddressPage => SectionMessages.English.transportArrangerSubHeading
      case FirstTransporterAddressPage => SectionMessages.English.firstTransporterSubHeading
      case DispatchAddressPage => SectionMessages.English.dispatchSubHeading
      case DestinationAddressPage => SectionMessages.English.destinationSubHeading
      case GuarantorAddressPage => SectionMessages.English.guarantorSubHeading
      case _ => SectionMessages.English.consigneeSubHeading
    }

    val property = "Property name or number (optional)"
    val propertyErrorLength = "Enter a property name or number up to 11 characters"
    val street = "Street name"
    val streetErrorRequired = "Enter a street name"
    val streetErrorLength = "Enter a street name up to 65 characters"
    val town = "Town or city"
    val townErrorRequired = "Enter a town or city"
    val townErrorLength = "Enter a town or city name up to 50 characters"
    val postcode = "Postcode"
    val postcodeErrorRequired = "Enter a postcode"
    val postcodeErrorLength = "Enter a postcode up to 10 characters"
  }

  object English extends ViewMessages with BaseEnglish
}
