/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.sections.consignor

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ConsignorPaidTemporaryAuthorisationCodeFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "consignorPaidTemporaryAuthorisationCode.error.required"
  val lengthKey = "consignorPaidTemporaryAuthorisationCode.error.length"
  val incorrectFormatKey = "consignorPaidTemporaryAuthorisationCode.error.incorrectFormat"

  val maxLength = 13

  val form = new ConsignorPaidTemporaryAuthorisationCodeFormProvider()()

  ".value" - {

    val fieldName = "value"

    "bind when the PTA code is valid" in {
      val result = form.bind(Map(fieldName -> "XIPTA12345678")).apply(fieldName)
      result.errors mustBe empty
    }

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind when the PTA code not in the correct format" in {
      val result = form.bind(Map(fieldName -> "GBWK123456789")).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, incorrectFormatKey, Seq("XIPTA[a-zA-Z0-9]{8}")))
    }

    "not bind when the PTA code has a symbol" in {
      val result = form.bind(Map(fieldName -> "XIPTA12-45678")).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, incorrectFormatKey, Seq("XIPTA[a-zA-Z0-9]{8}")))
    }

  }
}
