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

class DispatchWarehouseExciseFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "dispatchWarehouseExcise.error.required"
  val xssKey = "dispatchWarehouseExcise.error.xss"
  val lengthKey = "dispatchWarehouseExcise.error.length"
  val formatKey = "dispatchWarehouseExcise.error.format"
  val fixedLength = 13

  val form = new DispatchWarehouseExciseFormProvider()()

  ".value" - {

    val fieldName = "value"
    val formatError = FormError(fieldName, formatKey, Seq("[A-Z]{2}[a-zA-Z0-9]{11}"))

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "GB00123456789"
    )

    behave like fieldWithFixedLength(
      form,
      fieldName,
      lengthError = FormError(fieldName, lengthKey, Seq(fixedLength)),
      fixedLength
    )

    behave like fieldWithXSSCharacters(
      form,
      fieldName,
      requiredError = FormError(fieldName, xssKey, Seq(XSS_REGEX)),
      dataItem = "<javascript>!" //has to be 13 chars
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithERN(
      form,
      fieldName,
      formatError = formatError
    )
  }
}
