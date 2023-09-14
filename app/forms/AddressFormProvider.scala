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

import javax.inject.Inject
import forms.mappings.Mappings
import models.UserAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

class AddressFormProvider @Inject() extends Mappings {

  val propertyMax = 11
  val streetMax = 65
  val townMax = 50
  val postcodeMax = 10

  def apply(): Form[UserAddress] =
    Form(mapping(
      "property" -> optional(text()
        .verifying(maxLength(propertyMax, "consignorAddress.property.error.length"))),
      "street" -> text("consignorAddress.street.error.required")
        .verifying(maxLength(streetMax, "consignorAddress.street.error.length")),
      "town" -> text("consignorAddress.town.error.required")
        .verifying(maxLength(townMax, "consignorAddress.town.error.length")),
      "postcode" -> text("consignorAddress.postcode.error.required")
        .verifying(maxLength(postcodeMax, "consignorAddress.postcode.error.length"))
    )(UserAddress.apply)(UserAddress.unapply)
  )
}
