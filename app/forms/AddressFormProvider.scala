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

package forms

import forms.mappings.Mappings
import models.UserAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class AddressFormProvider @Inject() extends Mappings {

  val propertyMax = 11
  val streetMax = 65
  val townMax = 50
  val postcodeMax = 10

  def apply(): Form[UserAddress] =
    Form(mapping(
      "property" -> optional(text()
        .verifying(maxLength(propertyMax, s"address.property.error.length"))),
      "street" -> text(s"address.street.error.required")
        .verifying(maxLength(streetMax, s"address.street.error.length")),
      "town" -> text(s"address.town.error.required")
        .verifying(maxLength(townMax, s"address.town.error.length")),
      "postcode" -> text(s"address.postcode.error.required")
        .verifying(maxLength(postcodeMax, s"address.postcode.error.length"))
    )(UserAddress.apply)(UserAddress.unapply)
  )
}
