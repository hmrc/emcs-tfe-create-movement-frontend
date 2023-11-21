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

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

import scala.util.Random

class ItemProducerSizeFormProviderSpec extends IntFieldBehaviours {

  val requiredKey = "itemProducerSize.error.required"
  val lengthKey = "itemProducerSize.error.length"
  val min = 1
  val max = 15

  val form = new ItemProducerSizeFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      Random.between(min, max).toString
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, "itemProducerSize.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "itemProducerSize.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum = min,
      maximum = max,
      expectedError = FormError(fieldName, "itemProducerSize.error.outOfRange", Seq(min, max))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "itemProducerSize.error.required")
    )
  }
}
