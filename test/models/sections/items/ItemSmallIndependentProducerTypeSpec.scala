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

import base.SpecBase
import models.sections.items.ItemSmallIndependentProducerType._
import play.api.libs.json.JsString

class ItemSmallIndependentProducerTypeSpec extends SpecBase {

  ".values" - {
    "should return all the geographical indication options" in {
      ItemSmallIndependentProducerType.values mustBe Seq(
        CertifiedIndependentSmallProducer,
        SelfCertifiedIndependentSmallProducerAndConsignor,
        SelfCertifiedIndependentSmallProducerAndNotConsignor,
        NotApplicable,
        NotProvided
      )
    }
  }

  ".notProvidedValues" - {

    "should return Not provided and None" in {

      ItemSmallIndependentProducerType.notProvidedValues mustBe Seq(
        NotApplicable,
        NotProvided
      )
    }
  }

  "must read correctly with NotProvided being mapped to NotApplicable" in {
    ItemSmallIndependentProducerType.values.filterNot(_.equals(NotProvided)).foreach { value =>
      JsString(value.toString).as[ItemSmallIndependentProducerType] mustBe value
    }
    JsString(ItemSmallIndependentProducerType.NotProvided.toString).as[ItemSmallIndependentProducerType] mustBe NotApplicable
  }

}
