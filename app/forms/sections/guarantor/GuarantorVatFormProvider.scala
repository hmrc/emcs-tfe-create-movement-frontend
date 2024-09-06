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

package forms.sections.guarantor

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.mappings.Mappings
import forms.sections.guarantor.GuarantorVatFormProvider._
import models.VatNumberModel
import models.sections.guarantor.GuarantorArranger
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings

import javax.inject.Inject

class GuarantorVatFormProvider @Inject() extends Mappings {

  def apply(guarantorArranger: GuarantorArranger): Form[VatNumberModel] =
    Form(
      mapping(
        hasVatNumberField -> boolean(vatRadioRequired(guarantorArranger)),
        vatNumberField -> ConditionalMappings.mandatoryIfTrue(hasVatNumberField,
          text(vatNumberRequired)
            .transform[String](_.replace("-", "").replace(" ", ""), identity)
            .verifying(
              firstError(
                maxLength(14, vatNumberLength),
                regexp(ONLY_ALPHANUMERIC_REGEX, vatNumberAlphanumeric)
              )
            )
        )
      )(VatNumberModel.apply)(VatNumberModel.unapply)
    )
}

object GuarantorVatFormProvider {
  val hasVatNumberField = "hasVatNumber"
  val vatNumberField = "vatNumber"

  val vatRadioRequired: GuarantorArranger => String = arranger =>  s"guarantorVat.$arranger.error.radio.required"
  val vatNumberRequired = "guarantorVat.error.input.required"
  val vatNumberLength = "guarantorVat.error.input.length"
  val vatNumberAlphanumeric = "guarantorVat.error.input.alphanumeric"
}
