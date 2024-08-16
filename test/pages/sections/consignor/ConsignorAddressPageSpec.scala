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

package pages.sections.consignor

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import play.api.test.FakeRequest

class ConsignorAddressPageSpec extends SpecBase {

  val userAddress: UserAddress = testUserAddress.copy(businessName = Some("consignor name"))

  "value" - {
    "must return None" - {
      "when no answer" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ConsignorAddressPage.value mustBe None
      }
    }

    "must return Some(_) when there is an answer" - {
      "and trader known facts exist" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsignorAddressPage, userAddress))
        ConsignorAddressPage.value mustBe Some(testUserAddress.copy(businessName = Some(testMinTraderKnownFacts.traderName)))
      }
      "and trader known facts don't exist" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsignorAddressPage, userAddress), traderKnownFacts = None)
        ConsignorAddressPage.value mustBe Some(testUserAddress.copy(businessName = Some("consignor name")))
      }
    }
  }
}
