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

package pages.sections.dispatch

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import pages.sections.consignor.ConsignorAddressPage
import play.api.test.FakeRequest

class DispatchAddressPageSpec extends SpecBase {

  val userAddress: UserAddress = testUserAddress.copy(businessName = Some("dispatch name"))
  val consignorUserAddress: UserAddress = testUserAddress.copy(businessName = Some("consignor name"))

  "value" - {
    "must return None" - {
      "when no answer" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        DispatchAddressPage.value mustBe None
      }
    }

    "must return Some(_) when there is an answer" - {
      "when DispatchUseConsignorDetailsPage is false" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers.set(DispatchAddressPage, userAddress).set(DispatchUseConsignorDetailsPage, false)
        )
        DispatchAddressPage.value mustBe Some(testUserAddress.copy(businessName = Some("dispatch name")))
      }
      "when DispatchUseConsignorDetailsPage is true" - {
        "and trader known facts exist" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(DispatchAddressPage, userAddress).set(DispatchUseConsignorDetailsPage, true).set(ConsignorAddressPage, consignorUserAddress)
          )
          DispatchAddressPage.value mustBe Some(testUserAddress.copy(businessName = Some("testTraderName")))
        }
        "and trader known facts don't exist and Consignor address exists" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(DispatchAddressPage, userAddress).set(DispatchUseConsignorDetailsPage, true).set(ConsignorAddressPage, consignorUserAddress),
            traderKnownFacts = None
          )
          DispatchAddressPage.value mustBe Some(testUserAddress.copy(businessName = Some("consignor name")))
        }
        "and trader known facts don't exist and Consignor address doesn't exist" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(DispatchAddressPage, userAddress).set(DispatchUseConsignorDetailsPage, true),
            traderKnownFacts = None
          )
          DispatchAddressPage.value mustBe Some(testUserAddress.copy(businessName = Some("dispatch name")))
        }
      }
    }
  }
}
