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

import forms.XSS_REGEX
import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class ItemPackagingSealTypeFormProviderSpec extends BooleanFieldBehaviours {

  val form = new ItemPackagingSealTypeFormProvider()()

  "form" - {
    "packagingSealType field" - {
      "must error when no data is entered" in {
        val boundForm = form.bind(Map(ItemPackagingSealTypeFormProvider.packagingSealTypeField -> ""))

        boundForm.errors mustBe Seq(FormError(
          ItemPackagingSealTypeFormProvider.packagingSealTypeField,
          ItemPackagingSealTypeFormProvider.sealTypeRequiredErrorKey
        ))
      }

      "must error when there are invalid characters" in {
        val boundForm = form.bind(Map(ItemPackagingSealTypeFormProvider.packagingSealTypeField -> "<>"))

        boundForm.errors mustBe Seq(FormError(
          ItemPackagingSealTypeFormProvider.packagingSealTypeField,
          ItemPackagingSealTypeFormProvider.sealTypeInvalidErrorKey,
          Seq(XSS_REGEX)
        ))
      }

      "must error when the input is too long" in {
        val boundForm = form.bind(Map(ItemPackagingSealTypeFormProvider.packagingSealTypeField -> "1" * 36))

        boundForm.errors mustBe Seq(FormError(
          ItemPackagingSealTypeFormProvider.packagingSealTypeField,
          ItemPackagingSealTypeFormProvider.sealTypeLengthErrorKey,
          Seq(ItemPackagingSealTypeFormProvider.maxLengthTextBoxValue)
        ))

      }
    }
    "packagingSealInformation field" - {
      "must error when there are invalid characters" in {
        val boundForm = form.bind(Map(
          ItemPackagingSealTypeFormProvider.packagingSealTypeField -> "test",
          ItemPackagingSealTypeFormProvider.packagingSealInformationField -> "<>"
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemPackagingSealTypeFormProvider.packagingSealInformationField,
          ItemPackagingSealTypeFormProvider.answerInvalidErrorKey,
          Seq(XSS_REGEX)
        ))
      }

      "must error when the input is too long" in {
        val boundForm = form.bind(Map(
          ItemPackagingSealTypeFormProvider.packagingSealTypeField -> "test",
          ItemPackagingSealTypeFormProvider.packagingSealInformationField -> "1" * 351
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemPackagingSealTypeFormProvider.packagingSealInformationField,
          ItemPackagingSealTypeFormProvider.answerLengthErrorKey,
          Seq(ItemPackagingSealTypeFormProvider.maxLengthValue)
        ))

      }
    }
  }
}
