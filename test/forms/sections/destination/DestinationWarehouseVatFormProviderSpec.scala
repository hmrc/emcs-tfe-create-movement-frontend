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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.sections.info.movementScenario.MovementScenario.RegisteredConsignee
import pages.sections.info.DestinationTypePage
import play.api.data.{Form, FormError}

class DestinationWarehouseVatFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "destinationWarehouseVat.error.required"
  val lengthKey = "destinationWarehouseVat.error.length"
  val maxLength = 14

  val form: Form[String] = new DestinationWarehouseVatFormProvider().apply(RegisteredConsignee)(messages(
    applicationBuilder(Some(emptyUserAnswers.set(DestinationTypePage, RegisteredConsignee))).build()))

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

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
