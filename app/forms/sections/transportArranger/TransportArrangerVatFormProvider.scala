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

package forms.sections.transportArranger

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.mappings.Mappings
import forms.sections.transportArranger.TransportArrangerVatFormProvider.{hasVatNumberField, vatNumberField}
import models.VatNumberModel
import models.sections.transportArranger.TransportArranger
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}

import javax.inject.Inject

class TransportArrangerVatFormProvider @Inject() extends Mappings {

  def apply(transportArranger: TransportArranger): Form[VatNumberModel] =
    Form(
      mapping(
        hasVatNumberField -> boolean(s"transportArrangerVat.error.radio.$transportArranger.required"),
        vatNumberField -> optional(
          playText()
            .verifying(maxLength(14, "transportArrangerVat.error.length"))
            .transform[String](_.replace("-", "").replace(" ", ""), identity)
            .verifying(regexp(ONLY_ALPHANUMERIC_REGEX, "transportArrangerVat.error.alphanumeric"))
        )
      )(VatNumberModel.apply)(VatNumberModel.unapply)
        .transform[VatNumberModel](
          model => if(!model.hasVatNumber) model.copy(vatNumber = None) else model, identity
        )
    )
}

object TransportArrangerVatFormProvider {

  val hasVatNumberField = "hasVatNumber"
  val vatNumberField = "vatNumber"

  val vatNumberRequired = "transportArrangerVat.error.input.required"
}
