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
import forms.mappings.Mappings
import models.sections.items.ItemPackagingSealTypeModel
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class ItemPackagingSealTypeFormProvider @Inject() extends Mappings {

  import ItemPackagingSealTypeFormProvider._

  def apply(): Form[ItemPackagingSealTypeModel] =
    Form(
      mapping(
        packagingSealTypeField ->
          text(sealTypeRequiredErrorKey)
            .verifying(maxLength(maxLengthTextBoxValue, sealTypeLengthErrorKey))
            .verifying(regexp(XSS_REGEX, sealTypeInvalidErrorKey)),
        packagingSealInformationField ->
          optional(
            text(answerRequiredErrorKey)
              .verifying(maxLength(maxLengthValue, answerLengthErrorKey))
              .verifying(regexp(XSS_REGEX, answerInvalidErrorKey))
          )
      )(ItemPackagingSealTypeModel.apply)(ItemPackagingSealTypeModel.unapply)
    )
}

object ItemPackagingSealTypeFormProvider {
  val packagingSealTypeField: String = "packaging-seal-type"
  val packagingSealInformationField: String = "packaging-seal-information"

  val sealTypeRequiredErrorKey = "itemPackagingSealType.error.sealType.required"
  val sealTypeInvalidErrorKey = "itemPackagingSealType.error.sealType.invalid"
  val sealTypeLengthErrorKey = "itemPackagingSealType.error.sealType.length"

  val answerRequiredErrorKey = "itemPackagingSealType.error.required"
  val answerInvalidErrorKey = "itemPackagingSealType.error.invalid"
  val answerLengthErrorKey = "itemPackagingSealType.error.length"

  val maxLengthValue: Int = 350
  val maxLengthTextBoxValue: Int = 35
}

