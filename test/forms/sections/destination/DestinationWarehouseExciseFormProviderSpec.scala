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

package forms.sections.destination

import fixtures.messages.sections.destination.DestinationWarehouseExciseMessages
import forms.XSS_REGEX
import forms.behaviours.StringFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class DestinationWarehouseExciseFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite {

  val requiredKey = "destinationWarehouseExcise.error.required"
  val lengthKey = "destinationWarehouseExcise.error.length"
  val maxLength = 16
  val invalidCharactersKey = "destinationWarehouseExcise.error.invalidCharacters"

  val form = new DestinationWarehouseExciseFormProvider()()

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
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    Seq(DestinationWarehouseExciseMessages.English) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct required error message" in {

          messages("destinationWarehouseExcise.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct length error message" in {

          messages("destinationWarehouseExcise.error.length") mustBe
            messagesForLanguage.errorLength
        }

        "have the correct invalidCharacters error message" in {

          messages("destinationWarehouseExcise.error.invalidCharacter") mustBe
            messagesForLanguage.errorInvalidCharacters
        }
      }
    }
  }
}
