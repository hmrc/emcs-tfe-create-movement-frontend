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

package forms.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportVatMessages
import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class ConsigneeExportVatFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "consigneeExportVat.error.required"
  val lengthKey = "consigneeExportVat.error.length"
  val invalidKey = "consigneeExportVat.error.invalid"
  val maxLength = 16

  val form = new ConsigneeExportVatFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "0" * maxLength
    )

    behave like fieldWithAtLeastOneAlphanumeric(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey, Seq(ONLY_ALPHANUMERIC_REGEX))
    )

    behave like fieldWithXSSCharacters(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidKey, Seq(ONLY_ALPHANUMERIC_REGEX))
    )

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
  }

  "Error Messages" - {

    Seq(ConsigneeExportVatMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct required error message" in {

          msgs("consigneeExportVat.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct length error message" in {

          msgs("consigneeExportVat.error.length") mustBe
            messagesForLanguage.errorLength
        }

        "have the correct invalid character error message" in {

          msgs("consigneeExportVat.error.invalid") mustBe
            messagesForLanguage.errorInvalid
        }

        "must transform the inputted VAT removing any spaces" in {
          val result = form.bind(Map("value" -> "GB 123 456")).get
          result mustBe "GB123456"
        }

        "must transform the inputted VAT into uppercase" in {
          val result = form.bind(Map("value" -> "gb123456")).get
          result mustBe "GB123456"
        }

        "must transform the inputted VAT removing any dashes" in {
          val result = form.bind(Map("value" -> "gb1-234-56")).get
          result mustBe "GB123456"
        }

      }
    }
  }
}
