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

package forms.sections.transportArranger

import base.SpecBase
import forms.behaviours.OptionFieldBehaviours
import models.sections.info.movementScenario.MovementScenario.UnknownDestination
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.Consignee
import pages.sections.info.DestinationTypePage
import play.api.data.FormError
import play.api.test.FakeRequest

class TransportArrangerFormProviderSpec extends OptionFieldBehaviours with SpecBase {
  val fieldName = "value"
  val requiredKey = "transportArranger.error.required"
  val invalidKey = "error.invalid"

  ".value" - {
    val form = new TransportArrangerFormProvider()()(dataRequest(FakeRequest()))

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like optionsField[TransportArranger](
      form,
      fieldName,
      validValues = TransportArranger.values,
      invalidError = FormError(fieldName, invalidKey)
    )

    "when the destinationType is an UnknownDestination" - {
      val dr = dataRequest(
        request = FakeRequest(),
        answers = emptyUserAnswers.set(DestinationTypePage, UnknownDestination)
      )

      val form = new TransportArrangerFormProvider()()(dr)

      TransportArranger.valuesForUnknownDestination.foreach { enumValue =>
        s"must bind and validate the value $enumValue successfully" in {
          val result = form.bind(Map(fieldName -> enumValue.toString))
          result.hasErrors mustBe false
        }
      }

      s"must return an error when trying to bind and validate the value $Consignee" in {
        val result = form.bind(Map(fieldName -> Consignee.toString))
        result.errors must contain only FormError(fieldName, invalidKey)
      }
    }
  }
}
