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
import play.api.data.Forms.{mapping, optional}
import play.api.data.format.Formatter
import play.api.data.validation.Constraint
import play.api.data.{FieldMapping, Form, FormError}

import javax.inject.Inject

class AddressFormProvider @Inject() extends Mappings {

  val propertyMax = 11
  val streetMax = 65
  val townMax = 50
  val postcodeMax = 10

  def apply(page: QuestionPage[UserAddress], isConsignorPageOrUsingConsignorDetails: Boolean)(implicit request: DataRequest[_]): Form[UserAddress] =
    Form(mapping(
      "businessName" -> {
        if (isConsignorPageOrUsingConsignorDetails && request.traderKnownFacts.isDefined) {
          implicit val binder: Formatter[Option[String]] = new Formatter[Option[String]] {
            override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] =
              Right(Some(data.getOrElse(key, request.traderKnownFacts.get.traderName)))

            override def unbind(key: String, value: Option[String]): Map[String, String] = value.map(v => Map(key -> v)).getOrElse(Map.empty)
          }
          new FieldMapping[Option[String]]("businessName")
        } else {
          text(s"address.businessName.error.$page.required")
            .verifying(maxLength(182, s"address.businessName.error.$page.length"))
            .verifying(regexpUnlessEmpty(XSS_REGEX, s"address.businessName.error.$page.invalidCharacter"))
            .transform[Option[String]](Some(_), _.get)
        }
      },

      "property" -> optional(text()
        .verifying(maxLength(propertyMax, "address.property.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.property.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.property.error.invalid"))),

      "street" -> text("address.street.error.required")
        .verifying(maxLength(streetMax, "address.street.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.street.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.street.error.invalid"))
        .transform[Option[String]](Some(_), _.get),

      "town" -> text("address.town.error.required")
        .verifying(maxLength(townMax, "address.town.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.town.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.town.error.invalid"))
        .transform[Option[String]](Some(_), _.get),

      "postcode" -> text("address.postcode.error.required")
        .verifying(maxLength(postcodeMax, "address.postcode.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, "address.postcode.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "address.postcode.error.invalid"))
        .verifying(getExtraPostcodeValidationForPage(page): _*)
        .transform[Option[String]](Some(_), _.get)
    )(UserAddress.apply)(UserAddress.unapply)
    )

  private def getExtraPostcodeValidationForPage(page: Page)(implicit request: DataRequest[_]): Seq[Constraint[String]] = {
    def validate(ern: Option[String]): Seq[Constraint[String]] = {
      ern match {
        case Some(ern) if ern.startsWith(Constants.NI_PREFIX) => Seq(startsWith(XI_POSTCODE, s"address.postcode.error.$page.mustStartWithBT"))
        case Some(ern) if ern.startsWith(Constants.GB_PREFIX) => Seq(doesNotStartWith(XI_POSTCODE, s"address.postcode.error.$page.mustNotStartWithBT"))
        case _ => Seq.empty
      }
    }

    page match {
      case ConsignorAddressPage() => validate(Some(request.ern))
      case ConsigneeAddressPage => validate(ConsigneeExcisePage.value)
      case DispatchAddressPage => validate(DispatchWarehouseExcisePage.value)
      case DestinationAddressPage => validate(DestinationWarehouseExcisePage.value)
      case _ => Seq.empty
    }
  }
}