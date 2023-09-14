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

object ConsignorAddressMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Enter the consignor’s address"
    val title = titleHelper(heading)
    val subheading = "Consignor information"
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
