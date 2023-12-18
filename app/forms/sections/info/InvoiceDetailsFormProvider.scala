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

package forms.sections.info

import forms.mappings.Mappings
import models.sections.info.InvoiceDetailsModel
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

import java.time.LocalDate
import javax.inject.Inject

class InvoiceDetailsFormProvider @Inject() extends Mappings {

  // scalastyle:off magic.number
  private val earliestInvoiceDate = LocalDate.of(2000, 1, 1)
  // scalastyle:on magic.number

  def apply(): Form[InvoiceDetailsModel] =
    Form(mapping(
      "invoice-reference" -> text("invoiceDetails.invoice-reference.error.required")
        .verifying(maxLength(35, "invoiceDetails.invoice-reference.error.length")),
      "value" -> localDate(
        invalidKey = "invoiceDetails.value.error.invalid",
        allRequiredKey = "invoiceDetails.value.error.required.all",
        twoRequiredKey = "invoiceDetails.value.error.required.two",
        requiredKey = "invoiceDetails.value.error.required"
      )
        .verifying(
          firstError(
            fourDigitYear("invoiceDetails.value.error.yearNotFourDigits"),
            minDate(earliestInvoiceDate, "invoiceDetails.value.error.earliestDate")
          )
        )
    )(InvoiceDetailsModel.apply)(InvoiceDetailsModel.unapply))

}
