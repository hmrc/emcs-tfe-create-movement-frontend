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

package forms.sections.importInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import forms.behaviours.StringFieldBehaviours
import forms.{CUSTOMS_OFFICE_CODE_REGEX, XSS_REGEX}
import play.api.data.FormError
import play.api.test.FakeRequest

class ImportCustomsOfficeCodeFormProviderSpec extends SpecBase with StringFieldBehaviours with MovementSubmissionFailureFixtures {

  val requiredKey = "importCustomsOfficeCode.error.required"
  val lengthKey = "importCustomsOfficeCode.error.length"
  val xssKey = "importCustomsOfficeCode.error.invalidCharacter"
  val regexKey = "importCustomsOfficeCode.error.customsOfficeCodeRegex"
  val errorMsg704Key = "errors.704.importCustomsOfficeCode.input"
  val requiredLength = 8

  val form = new ImportCustomsOfficeCodeFormProvider().apply()(dataRequest(FakeRequest()))

  ".value" - {

    val fieldName = "value"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithFixedLength(
      form,
      fieldName,
      lengthError = FormError(fieldName, lengthKey, Seq(requiredLength)),
      requiredLength = requiredLength
    )

    s"not bind a value that contains XSS chars" in {

      val boundForm = form.bind(Map(fieldName -> "<1234567"))
      boundForm.errors mustBe Seq(FormError(fieldName, xssKey, Seq(XSS_REGEX)))
    }

    s"not bind a value that doesn't start with two alpha chars" in {

      val boundForm = form.bind(Map(fieldName -> "12345678"))
      boundForm.errors mustBe Seq(FormError(fieldName, regexKey, Seq(CUSTOMS_OFFICE_CODE_REGEX)))
    }

    s"bind a value that meets the expected regex" in {

      val boundForm = form.bind(Map(fieldName -> "GB345678"))
      boundForm.errors mustBe Seq()
      boundForm.value mustBe Some("GB345678")
    }

    "not bind a value that matches the existing answer when a 704 has been returned for that field" in {

      val form = new ImportCustomsOfficeCodeFormProvider().apply()(
        dataRequest(FakeRequest(), emptyUserAnswers.copy(submissionFailures = Seq(importCustomsOfficeCodeFailure)))
      )

      val boundForm = form.bind(Map(fieldName -> testImportCustomsOffice))
      boundForm.errors mustBe Seq(FormError(fieldName, errorMsg704Key, Seq()))
    }
  }
}
