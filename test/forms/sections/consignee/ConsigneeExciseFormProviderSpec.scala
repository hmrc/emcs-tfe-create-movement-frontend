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

package forms.sections.consignee

import base.SpecBase
import forms.ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.test.FakeRequest

class ConsigneeExciseFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite with SpecBase {

  val fieldName = "value"
  val fixedLength = 13

  "ConsigneeExciseFormProvider" - {

    val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(FakeRequest()))
    val dynamicForm = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = true)(dataRequest(FakeRequest()))

    "when a value is not provided" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.noInput", Seq()))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.noInput", Seq()))
      }
    }

    "when the value contains invalid characters" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> "!@£$%^&*()_+!"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
    }

    "for the ERN form" - {
      behave like fieldWithERN(
        form = form,
        fieldName = fieldName,
        formatError = FormError(fieldName, "consigneeExcise.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}"))
      )

      behave like fieldWithFixedLength(
        form,
        fieldName,
        lengthError = FormError(fieldName, "consigneeExcise.error.length", Seq(fixedLength)),
        fixedLength
      )
    }

    "for the temporary authorisation reference form" - {
      behave like fieldWithERN(
        form = dynamicForm,
        fieldName = fieldName,
        formatError = FormError(fieldName, "consigneeExcise.temporaryConsignee.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}"))
      )

      behave like fieldWithFixedLength(
        dynamicForm,
        fieldName,
        lengthError = FormError(fieldName, "consigneeExcise.temporaryConsignee.error.length", Seq(fixedLength)),
        fixedLength
      )
    }
  }
}
