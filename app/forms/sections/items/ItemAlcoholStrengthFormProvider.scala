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

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class ItemAlcoholStrengthFormProvider @Inject() extends Mappings {

  import ItemAlcoholStrengthFormProvider._

  def apply(): Form[BigDecimal] =
    Form(
      fieldName -> text(requiredErrorKey)
        .transform[String](_.replace(" ", ""), identity)
        .verifying(isDecimal(nonNumericErrorKey))
        .transform[BigDecimal](BigDecimal(_), _.toString())
        .verifying(
          firstError(
            decimalRange(minValue, maxValue, rangeErrorKey),
            maxDecimalPlaces(maxDecimalPlacesValue, maxDecimalPlacesErrorKey)
          )
        )
    )
}

object ItemAlcoholStrengthFormProvider {

  val maxValue = BigDecimal(100)
  val minValue = BigDecimal(0.5)
  val maxDecimalPlacesValue = 2

  val fieldName = "value"

  val requiredErrorKey = "itemAlcoholStrength.error.required"
  val nonNumericErrorKey = "itemAlcoholStrength.error.nonNumeric"
  val rangeErrorKey = "itemAlcoholStrength.error.range"
  val maxDecimalPlacesErrorKey = "itemAlcoholStrength.error.decimalPlaces"

}