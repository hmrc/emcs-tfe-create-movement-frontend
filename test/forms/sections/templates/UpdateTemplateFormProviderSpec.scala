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

package forms.sections.templates

import base.SpecBase
import fixtures.messages.sections.templates.UpdateTemplateMessages
import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class UpdateTemplateFormProviderSpec extends SpecBase with BooleanFieldBehaviours {

  val requiredKey = "updateTemplate.error.required"
  val invalidKey = "error.boolean"

  val form = new UpdateTemplateFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  Seq(UpdateTemplateMessages.English).foreach { messages =>

    implicit val msgs: Messages = messagesApi.preferred(Seq(messages.lang))

    s"Must return the correct error messages when rendered in lang code '${messages.lang.code}'" - {

      msgs(requiredKey) mustBe "Select yes if you would like to update this template"
    }
  }
}
