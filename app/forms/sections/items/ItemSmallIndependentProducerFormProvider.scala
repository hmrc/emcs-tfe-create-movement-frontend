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
import forms.sections.items.ItemSmallIndependentProducerFormProvider._
import models.sections.items.ItemSmallIndependentProducerType.SelfCertifiedIndependentSmallProducerAndNotConsignor
import models.sections.items.{ItemSmallIndependentProducerModel, ItemSmallIndependentProducerType}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIf

import javax.inject.Inject

class ItemSmallIndependentProducerFormProvider @Inject() extends Mappings {

  def apply(): Form[ItemSmallIndependentProducerModel] =
    Form(
      mapping(
        producerField -> enumerable[ItemSmallIndependentProducerType](producerRequiredError),
        producerIdField -> mandatoryIf(isOptionSelected(producerField, SelfCertifiedIndependentSmallProducerAndNotConsignor.toString),
          text(producerIdRequiredError)
            .verifying(
              firstError(
                maxLength(producerIdMaxLength, producerIdMaxLengthError),
                regexpUnlessEmpty(XSS_REGEX, producerIdInvalidError)
              )
            )
        )
      )(ItemSmallIndependentProducerModel.apply)(ItemSmallIndependentProducerModel.unapply)
    )

}

object ItemSmallIndependentProducerFormProvider {

  val producerField = "producer"

  val producerIdField = "producerId"

  val producerIdMaxLength = 16

  val producerRequiredError = "itemSmallIndependentProducer.error.producer.required"

  val producerIdRequiredError = "itemSmallIndependentProducer.error.producerId.required"

  val producerIdMaxLengthError = "itemSmallIndependentProducer.error.producerId.length"

  val producerIdInvalidError = "itemSmallIndependentProducer.error.producerId.invalid"
}