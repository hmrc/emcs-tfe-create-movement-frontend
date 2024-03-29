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

package forms.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemWineProductCategoryMessages
import forms.behaviours.OptionFieldBehaviours
import models.sections.items.ItemWineProductCategory
import play.api.data.FormError
import play.api.i18n.Messages

class ItemWineProductCategoryFormProviderSpec extends SpecBase with OptionFieldBehaviours {

  val requiredKey = "itemWineProductCategory.error.required"

  val form = new ItemWineProductCategoryFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like optionsField[ItemWineProductCategory](
      form,
      fieldName,
      validValues = ItemWineProductCategory.allValues,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    Seq(ItemWineProductCategoryMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for required key" in {

          msgs(requiredKey) mustBe messagesForLanguage.errorRequired
        }
      }
    }
  }
}
