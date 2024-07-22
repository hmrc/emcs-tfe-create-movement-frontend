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

import config.Constants
import config.Constants.XI_POSTCODE
import forms.mappings.Mappings
import models.UserAddress
import models.requests.DataRequest
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeExcisePage}
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.destination.{DestinationAddressPage, DestinationWarehouseExcisePage}
import pages.sections.dispatch.{DispatchAddressPage, DispatchWarehouseExcisePage}
import pages.{Page, QuestionPage}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}
import play.api.data.validation.Constraint

import javax.inject.Inject

class AddressFormProvider @Inject() extends Mappings {

  val propertyMax = 11
  val streetMax = 65
  val townMax = 50
  val postcodeMax = 10

  def apply(page: QuestionPage[UserAddress])(implicit request: DataRequest[_]): Form[UserAddress] =
    Form(mapping(
      "property" -> optional(text()
        .verifying(maxLength(propertyMax, "address.property.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.property.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.property.error.invalid"))),

      "street" -> text("address.street.error.required")
        .verifying(maxLength(streetMax, "address.street.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.street.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.street.error.invalid")),

      "town" -> text("address.town.error.required")
        .verifying(maxLength(townMax, "address.town.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.town.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.town.error.invalid")),

      "postcode" -> text("address.postcode.error.required")
        .verifying(maxLength(postcodeMax, "address.postcode.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.postcode.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.postcode.error.invalid"))
        .verifying(getExtraPostcodeValidationForPage(page): _*)
    )(UserAddress.apply)(UserAddress.unapply)
    )

  private def getExtraPostcodeValidationForPage(page: Page)(implicit request: DataRequest[_]): Seq[Constraint[String]] = {

    def niPostcode(isNi: Boolean, page: Page): Seq[Constraint[String]] =
      if (isNi) Seq(startsWith(XI_POSTCODE, s"address.postcode.error.$page.mustStartWithBT")) else {
        Seq(doesNotStartWith(XI_POSTCODE, s"address.postcode.error.$page.mustNotStartWithBT"))
      }

    page match {
      case ConsignorAddressPage => niPostcode(request.isNorthernIrelandErn, page)
      case ConsigneeAddressPage if ConsigneeExcisePage.value.nonEmpty =>
        niPostcode(ConsigneeExcisePage.value.exists(_.startsWith(Constants.NI_PREFIX)), page)
      case DispatchAddressPage if DispatchWarehouseExcisePage.value.nonEmpty =>
        niPostcode(DispatchWarehouseExcisePage.value.exists(_.startsWith(Constants.NI_PREFIX)), page)
      case DestinationAddressPage if DestinationWarehouseExcisePage.value.nonEmpty =>
        niPostcode(DestinationWarehouseExcisePage.value.exists(_.startsWith(Constants.NI_PREFIX)), page)
      case _ => Seq.empty
    }
  }
}
