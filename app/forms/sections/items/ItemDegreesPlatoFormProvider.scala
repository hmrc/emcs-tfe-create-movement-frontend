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

import forms.mappings.Mappings
import models.Index
import models.requests.DataRequest
import models.sections.items.ItemDegreesPlatoModel
import pages.sections.items.ItemDegreesPlatoPage
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfTrue

import javax.inject.Inject

class ItemDegreesPlatoFormProvider @Inject() extends Mappings {

  import ItemDegreesPlatoFormProvider._

  def apply(idx: Index)(implicit request: DataRequest[_]): Form[ItemDegreesPlatoModel] =
    Form(
      mapping(
        hasDegreesPlatoField -> boolean(radioRequired),
        degreesPlatoField -> mandatoryIfTrue(hasDegreesPlatoField,
          text(requiredErrorKey)
            .verifying(isDecimal(nonNumericErrorKey))
            .transform[BigDecimal](BigDecimal(_), _.toString())
            .verifying(
              firstError(
                decimalRange(minValue, maxValue, rangeErrorKey),
                maxDecimalPlaces(maxDecimalPlacesValue, maxDecimalPlacesErrorKey),
                isNotEqualToOptExistingAnswer(
                  existingAnswer = ItemDegreesPlatoPage(idx).getOriginalAttributeValue.map(BigDecimal(_)),
                  errorKey = sameInputAsOriginalSubmissionErrorKey
                )
              )
            )
        )
      )(ItemDegreesPlatoModel.apply)(ItemDegreesPlatoModel.unapply)
    )
}

object ItemDegreesPlatoFormProvider {

  val maxValue = BigDecimal(999.99)
  val minValue = BigDecimal(0.01)
  val maxDecimalPlacesValue = 2

  val hasDegreesPlatoField: String = "hasDegreesPlato"
  val degreesPlatoField: String = "degreesPlato"

  val radioRequired = "itemDegreesPlato.radio.error.required"
  val requiredErrorKey = "itemDegreesPlato.amount.error.required"
  val nonNumericErrorKey = "itemDegreesPlato.amount.error.nonNumeric"
  val rangeErrorKey = "itemDegreesPlato.amount.error.range"
  val maxDecimalPlacesErrorKey = "itemDegreesPlato.amount.error.decimalPlaces"
  val sameInputAsOriginalSubmissionErrorKey = "errors.704.items.degreesPlato.input"
}
