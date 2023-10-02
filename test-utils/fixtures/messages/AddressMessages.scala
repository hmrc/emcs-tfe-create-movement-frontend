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
import pages.QuestionPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.transportArranger.TransportArrangerAddressPage

object AddressMessages {

  trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = (page: QuestionPage[UserAddress]) => page match {
      case ConsignorAddressPage => "Enter the consignor’s address"
      case _  => "Enter the consignee’s address"
    }

    val title = (page: QuestionPage[UserAddress]) => titleHelper(heading(page))

    val transportArrangerAddressGoodsOwnerHeading = "Enter the good owner’s business address"
    val transportArrangerAddressGoodsOwnerTitle = titleHelper(transportArrangerAddressGoodsOwnerHeading)
    val transportArrangerAddressOtherHeading = "Enter the transporter’s business address"
    val transportArrangerAddressOtherTitle = titleHelper(transportArrangerAddressOtherHeading)

    val subheading = (page: QuestionPage[UserAddress]) => page match {
      case ConsignorAddressPage => "Consignor information"
      case TransportArrangerAddressPage => "Guarantor"
      case _ => "Consignee information"
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

  object Welsh extends ViewMessages with BaseWelsh
}
