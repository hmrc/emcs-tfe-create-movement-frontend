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
    def heading(goodsType: String) = s"Can you confirm that the producer of the $goodsType is certified as an independent small producer?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val yesBeer = "Yes - It is hereby certified that the product described has been produced by an independent small brewery"
    val yesSpirits = "Yes - It is hereby certified that the product described has been produced by an independent small distillery"
    val yesOther = "Yes - It is hereby certified that the product described has been produced by an independent small producer"

    //TODO: TBC by UX as not in prototype or copy decks yet
    val cyaLabel = "Independent small producer"
    val cyaChangeHidden = "independent small producer"
  }

  object English extends ViewMessages with BaseEnglish

}
