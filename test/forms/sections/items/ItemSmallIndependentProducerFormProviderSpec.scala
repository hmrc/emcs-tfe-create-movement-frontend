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

import fixtures.BaseFixtures
import forms.XSS_REGEX
import forms.behaviours.FieldBehaviours
import forms.sections.items.ItemSmallIndependentProducerFormProvider._
import models.sections.items.{ItemSmallIndependentProducerModel, ItemSmallIndependentProducerType}
import models.sections.items.ItemSmallIndependentProducerType.{NotProvided, SelfCertifiedIndependentSmallProducerAndNotConsignor}
import play.api.data.FormError

class ItemSmallIndependentProducerFormProviderSpec extends FieldBehaviours with BaseFixtures {

  val requiredKey = "itemSmallIndependentProducer.error.required"
  val invalidKey = "error.boolean"

  val maxLengthForInput = 16

  val form = new ItemSmallIndependentProducerFormProvider()()

  ".apply" - {

    "should bind successfully" - {

      ItemSmallIndependentProducerType.values
        .filterNot(_ == SelfCertifiedIndependentSmallProducerAndNotConsignor)
        .filterNot(_ == NotProvided)
        .foreach { producer =>

        s"when the producer is: $producer" in {

          form.bind(Map(
            producerField -> producer.toString
          )).get mustBe ItemSmallIndependentProducerModel(producerType = producer, producerId = None)
        }
      }

      s"when the producer is $SelfCertifiedIndependentSmallProducerAndNotConsignor and an input value has been provided" in {

        form.bind(Map(
          producerField -> SelfCertifiedIndependentSmallProducerAndNotConsignor.toString,
          producerIdField -> testVatNumber
        )).get mustBe ItemSmallIndependentProducerModel(producerType = SelfCertifiedIndependentSmallProducerAndNotConsignor, producerId = Some(testVatNumber))
      }
    }

    "should not bind" - {

      "when no producer has been selected" in {

        form.bind(Map.empty[String, String]).errors mustBe Seq(FormError(producerField, List(producerRequiredError), List()))
      }

      s"when $SelfCertifiedIndependentSmallProducerAndNotConsignor has been selected but no input has been provided" in {

        form.bind(Map(
          producerField -> SelfCertifiedIndependentSmallProducerAndNotConsignor.toString
        )).errors mustBe Seq(FormError(producerIdField, List(producerIdRequiredError), List()))
      }

      s"when $SelfCertifiedIndependentSmallProducerAndNotConsignor has been selected and the input is too long" in {

        form.bind(Map(
          producerField -> SelfCertifiedIndependentSmallProducerAndNotConsignor.toString,
          producerIdField -> "a" * (maxLengthForInput + 1)
        )).errors mustBe Seq(FormError(producerIdField, List(producerIdMaxLengthError), List(producerIdMaxLength)))
      }

      s"when $SelfCertifiedIndependentSmallProducerAndNotConsignor has been selected and the input is invalid" in {

        form.bind(Map(
          producerField -> SelfCertifiedIndependentSmallProducerAndNotConsignor.toString,
          producerIdField -> ";"
        )).errors mustBe Seq(FormError(producerIdField, List(producerIdInvalidError), List(XSS_REGEX)))
      }
    }
  }
}
