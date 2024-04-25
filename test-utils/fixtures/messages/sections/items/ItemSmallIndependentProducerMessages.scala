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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemSmallIndependentProducerMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Independent small producer declaration"
    val title: String = titleHelper(heading)

    val producedByIndependentSmallProducer = "It is hereby certified that the alcoholic product described has been produced by an independent small producer."
    val producedByIndependentSmallBrewery = "It is hereby certified that the alcoholic product described has been produced by an independent small brewery."
    val producedByIndependentSmallDistillery = "It is hereby certified that the alcoholic product described has been produced by an independent small distillery."
    val producedByIndependentWineProducer = "It is hereby certified that the alcoholic product described has been produced by an independent wine producer."
    val producedByIndependentFermentedBeveragesProducer = "It is hereby certified that the alcoholic product described has been produced by an independent producer of fermented beverages other than wine and beer."
    val producedByIndependentIntermediateProductsProducer = "It is hereby certified that the alcoholic product described has been produced by an independent intermediate products producer."

    val legend = "Who is the independent small producer?"

    val certifiedIndependentSmallProducer = "The producer is a certified independent small producer"
    val certifiedIndependentSmallProducerHint = "The certificate should be recorded in the Documents section of the eAD."
    val selfCertifiedIndependentSmallProducerAndConsignor = "The producer is a self-certified independent small producer and the consignor"
    val selfCertifiedIndependentSmallProducerNotConsignor = "The producer is a self-certified independent small producer and not the consignor"
    val selfCertifiedIndependentSmallProducerNotConsignorInput = "Enter the self-certified producer’s excise ID or, if not available, the VAT registration number"
    val notAIndependentSmallProducer = "The producer is not an independent small producer"
    val smallProducerNotProvided = "I don’t want to provide information about the producer"

    def seedNumber(value: String) = s"Seed number: $value"

    val cyaLabel = "Independent small producer declaration"
    val cyaChangeHidden = "Independent small producer declaration"
  }

  object English extends ViewMessages with BaseEnglish

}
