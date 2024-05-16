/*
 * Copyright 2024 HM Revenue & Customs
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

package models.sections.items

import models.{Enumerable, WithName}

sealed trait ItemSmallIndependentProducerType

case object ItemSmallIndependentProducerType extends Enumerable.Implicits {

  case object CertifiedIndependentSmallProducer extends WithName("CertifiedProducer") with ItemSmallIndependentProducerType

  case object SelfCertifiedIndependentSmallProducerAndConsignor extends WithName("SelfCertifiedProducerAndConsignor") with ItemSmallIndependentProducerType

  case object SelfCertifiedIndependentSmallProducerAndNotConsignor extends WithName("SelfCertifiedProducerAndNotConsignor") with ItemSmallIndependentProducerType

  case object NotAIndependentSmallProducer extends WithName("None") with ItemSmallIndependentProducerType

  case object NotProvided extends WithName("NotProvided") with ItemSmallIndependentProducerType

  val values: Seq[ItemSmallIndependentProducerType] = Seq(
    CertifiedIndependentSmallProducer,
    SelfCertifiedIndependentSmallProducerAndConsignor,
    SelfCertifiedIndependentSmallProducerAndNotConsignor,
    NotAIndependentSmallProducer,
    NotProvided
  )

  val notProvidedValues: Seq[ItemSmallIndependentProducerType] = Seq(
    NotAIndependentSmallProducer,
    NotProvided
  )

  implicit val enumerable: Enumerable[ItemSmallIndependentProducerType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
