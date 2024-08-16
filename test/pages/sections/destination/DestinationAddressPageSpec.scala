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

package pages.sections.destination

import base.SpecBase
import models.requests.DataRequest
import pages.sections.destination.DestinationAddressPage.DestinationBusinessNamePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DestinationAddressPageSpec extends SpecBase {

  "DestinationAddressPage" - {

    "return the address with business name when name is in the address model is present" in {
      val userAddress = testUserAddress
      val userAnswers = emptyUserAnswers.set(DestinationAddressPage, userAddress).set(DestinationBusinessNamePage, "BusinessName")
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

      DestinationAddressPage.value mustBe Some(userAddress)
    }

    "return the address with business name when DestinationBusinessNamePage is present" in {
      val userAddress = testUserAddress.copy(businessName = None)
      val userAnswers = emptyUserAnswers.set(DestinationAddressPage, userAddress).set(DestinationBusinessNamePage, "BusinessName")
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

      DestinationAddressPage.value mustBe Some(userAddress.copy(businessName = Some("BusinessName")))
    }

    "return the address without business name when DestinationBusinessNamePage is not present" in {
      val userAddress = testUserAddress.copy(businessName = None)
      val userAnswers = emptyUserAnswers.set(DestinationAddressPage, userAddress)
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

      DestinationAddressPage.value mustBe Some(userAddress)
    }

    "return None when DestinationAddressPage is not present" in {
      val userAnswers = emptyUserAnswers
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

      DestinationAddressPage.value mustBe None
    }
  }
}