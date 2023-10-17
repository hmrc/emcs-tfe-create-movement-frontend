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

package forms.sections.transportUnit

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import models.TransportUnitType
import play.api.data.FormError

class TransportUnitIdentityFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "transportUnitIdentity.error.required"
  val lengthKey = "transportUnitIdentity.error.length"
  val maxLength = 35
  val invalidCharactersKey = "transportUnitIdentity.error.invalidCharacters"




  ".value" - {
    Seq(
      ("FixedTransport", TransportUnitType.FixedTransport, 5),
      ("Tractor", TransportUnitType.Tractor, 4),
      ("Trailer" , TransportUnitType.Trailer, 3),
      ("Vehicle", TransportUnitType.Vehicle, 2),
      ("Container", TransportUnitType.Container, 1),
    ) foreach { case (name, transportUnitType, value) =>
      s"when bound for $name" - {

        val form = new TransportUnitIdentityFormProvider()(transportUnitType)

        val fieldName = "value"

        behave like fieldThatBindsValidData(
          form,
          fieldName,
          "0" * maxLength
        )

        behave like fieldWithMaxLength(
          form,
          fieldName,
          maxLength = maxLength,
          lengthError = FormError(fieldName, s"$lengthKey.$value", Seq(maxLength))
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, s"$requiredKey.$value")
        )

        "must not bind if non alphanumeric present" in {
          val formResult = form.bind(Map("value" -> "*"))
          formResult.errors mustBe Seq(FormError(fieldName, s"$invalidCharactersKey.$value", Seq(ONLY_ALPHANUMERIC_REGEX)))
        }
      }
    }
  }
}
