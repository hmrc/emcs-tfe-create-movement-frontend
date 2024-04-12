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
import forms.sections.transportArranger.TransportArrangerVatFormProvider.{hasVatNumberField, transportArrangerVatNumberField}
import models.sections.transportArranger.{TransportArranger, TransportArrangerVatModel}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}

import javax.inject.Inject

class TransportArrangerVatFormProvider @Inject() extends Mappings {

  def apply(transportArranger: TransportArranger): Form[TransportArrangerVatModel] =
    Form(
      mapping(
        hasVatNumberField -> boolean(s"transportArrangerVat.error.radio.$transportArranger.required"),
        transportArrangerVatNumberField -> optional(
          playText()
            .verifying(maxLength(14, "transportArrangerVat.error.length"))
            .transform[String](_.replace("-", "").replace(" ", ""), identity)
            .verifying(regexp(ONLY_ALPHANUMERIC_REGEX, "transportArrangerVat.error.alphanumeric"))
        )
      )(TransportArrangerVatModel.apply)(TransportArrangerVatModel.unapply)
        .transform[TransportArrangerVatModel](
          model => if(!model.hasTransportArrangerVatNumber) model.copy(transportArrangerVatNumber = None) else model, identity
        )
    )
}

object TransportArrangerVatFormProvider {

  val hasVatNumberField = "hasTransportArrangerVatNumber"
  val transportArrangerVatNumberField = "transportArrangerVatNumber"

  val transportArrangerVatNumberRequired = "transportArrangerVat.error.input.required"
}
