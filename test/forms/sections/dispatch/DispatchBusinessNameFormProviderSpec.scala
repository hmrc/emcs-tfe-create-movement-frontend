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

package forms.sections.dispatch

import forms.XSS_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class DispatchBusinessNameFormProviderSpec extends StringFieldBehaviours {

  val noInputKey = "dispatchBusinessName.error.noInput"
  val lengthKey = "dispatchBusinessName.error.tooLong"
  val invalidCharactersKey = "dispatchBusinessName.error.invalidCharacters"
  val maxLength = 182

  val form = new DispatchBusinessNameFormProvider()()

  ".value" - {

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
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithXSSCharacters(
      form,
      fieldName,
      FormError(fieldName, invalidCharactersKey, Seq(XSS_REGEX))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, noInputKey)
    )
  }
}
