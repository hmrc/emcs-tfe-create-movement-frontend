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

package forms.sections.destination

import config.Constants
import config.Constants.XI_POSTCODE
import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import models.UserAddress
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.DirectDelivery
import pages.sections.destination.{DestinationAddressPage, DestinationWarehouseExcisePage}
import pages.sections.info.DestinationTypePage
import pages.{Page, QuestionPage}
import play.api.data.Forms.{mapping, optional}
import play.api.data.validation.Constraint
import play.api.data.{Form, Mapping}

import javax.inject.Inject

class DestinationAddressFormProvider @Inject() extends Mappings {

  val businessNameMax = 182
  val propertyMax = 11
  val streetMax = 65
  val townMax = 50
  val postcodeMax = 10

  private val page: QuestionPage[UserAddress] = DestinationAddressPage

  private def isDirectDelivery(implicit request: DataRequest[_]): Boolean = DestinationTypePage.value.contains(DirectDelivery)

  def apply()(implicit request: DataRequest[_]): Form[UserAddress] = {
    Form(mapping(
      "businessName" -> businessNameValidation,
      "property" -> propertyValidation,
      "street" -> streetValidation,
      "town" -> townValidation,
      "postcode" -> postcodeValidation
    )(UserAddress.apply)(UserAddress.unapply)
    )
  }

  private def businessNameValidation(implicit request: DataRequest[_]): Mapping[Option[String]] = {
    val validation = text(s"address.businessName.error.$page.required")
      .verifying(maxLength(businessNameMax, s"address.businessName.error.$page.length"))
      .verifying(regexpUnlessEmpty(XSS_REGEX, s"address.businessName.error.$page.invalid"))

    if (isDirectDelivery) {
      optional(validation)
    } else {
      validation.transform[Option[String]](Some(_), _.get)
    }
  }

  private def propertyValidation: Mapping[Option[String]] = {
    val validation = text()
      .verifying(maxLength(propertyMax, "address.property.error.length"))
      .verifying(regexp(ALPHANUMERIC_REGEX, "address.property.error.character"))
      .verifying(regexpUnlessEmpty(XSS_REGEX, "address.property.error.invalid"))

    optional(validation)
  }

  private def streetValidation(implicit request: DataRequest[_]): Mapping[Option[String]] = {
    val validation = text("address.street.error.required")
      .verifying(maxLength(streetMax, "address.street.error.length"))
      .verifying(regexp(ALPHANUMERIC_REGEX, "address.street.error.character"))
      .verifying(regexpUnlessEmpty(XSS_REGEX, "address.street.error.invalid"))

    if (isDirectDelivery) {
      validation.transform[Option[String]](Some(_), _.get)
    } else {
      optional(validation)
    }
  }

  private def townValidation(implicit request: DataRequest[_]): Mapping[Option[String]] = {
    val validation = text("address.town.error.required")
      .verifying(maxLength(townMax, "address.town.error.length"))
      .verifying(regexp(ALPHANUMERIC_REGEX, "address.town.error.character"))
      .verifying(regexpUnlessEmpty(XSS_REGEX, "address.town.error.invalid"))

    if (isDirectDelivery) {
      validation.transform[Option[String]](Some(_), _.get)
    } else {
      optional(validation)
    }
  }

  private def postcodeValidation(implicit request: DataRequest[_]): Mapping[Option[String]] = {
    val validation = text("address.postcode.error.required")
      .verifying(maxLength(postcodeMax, "address.postcode.error.length"))
      .verifying(regexp(ALPHANUMERIC_REGEX, "address.postcode.error.character"))
      .verifying(regexpUnlessEmpty(XSS_REGEX, "address.postcode.error.invalid"))
      .verifying(getExtraPostcodeValidationForPage(page): _*)

    if (isDirectDelivery) {
      validation.transform[Option[String]](Some(_), _.get)
    } else {
      optional(validation)
    }
  }

  private def getExtraPostcodeValidationForPage(page: Page)(implicit request: DataRequest[_]): Seq[Constraint[String]] = {
    DestinationWarehouseExcisePage.value match {
      case Some(ern) if ern.startsWith(Constants.NI_PREFIX) => Seq(startsWith(XI_POSTCODE, s"address.postcode.error.$page.mustStartWithBT"))
      case Some(ern) if ern.startsWith(Constants.GB_PREFIX) => Seq(doesNotStartWith(XI_POSTCODE, s"address.postcode.error.$page.mustNotStartWithBT"))
      case _ => Seq.empty
    }
  }
}