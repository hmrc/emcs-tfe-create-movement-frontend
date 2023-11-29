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
import fixtures.messages.sections.items.ItemPackagingProductTypeMessages.English
import forms.behaviours.{OptionFieldBehaviours, StringFieldBehaviours}
import models.sections.items.PackagingProductType
import play.api.data.FormError
import play.api.i18n.Messages

class ItemPackagingProductTypeFormProviderSpec extends OptionFieldBehaviours with SpecBase with StringFieldBehaviours {

  val form = new ItemPackagingProductTypeFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "itemPackagingProductType.error.required"

    behave like optionsField[PackagingProductType](
      form,
      fieldName,
      validValues  = PackagingProductType.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "Error Messages" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      s"when output for language code '${English.lang.code}'" - {

        "have the correct error message for required" in {

          msgs(requiredKey) mustBe
            English.errorRequired
        }
      }
    }
  }
}
