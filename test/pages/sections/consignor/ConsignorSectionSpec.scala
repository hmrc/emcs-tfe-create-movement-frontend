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

package pages.sections.consignor

import base.SpecBase
import models.requests.DataRequest
import play.api.test.FakeRequest

class ConsignorSectionSpec extends SpecBase {

  "isCompleted" - {

    "for a NorthernIrelandCertifiedConsignor logged in trader" - {

      "must return true" - {
        "when both the PTA code and address have been provided" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers
              .set(ConsignorPaidTemporaryAuthorisationCodePage, testPaidTemporaryAuthorisationCode)
              .set(ConsignorAddressPage, testUserAddress),
            ern = testNICertifiedConsignorErn
          )

          ConsignorSection.isCompleted mustBe true
        }
      }

      "must return false" - {
        "when only the PTA code has been entered and no address" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers
              .set(ConsignorPaidTemporaryAuthorisationCodePage, testPaidTemporaryAuthorisationCode),
            ern = testNICertifiedConsignorErn
          )

          ConsignorSection.isCompleted mustBe false
        }
        "when only the address has been entered and no PTA code" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers
              .set(ConsignorAddressPage, testUserAddress),
            ern = testNICertifiedConsignorErn
          )

          ConsignorSection.isCompleted mustBe false
        }
        "when neither the PTA code or address has been entered" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers,
            ern = testNICertifiedConsignorErn)

          ConsignorSection.isCompleted mustBe false
        }

      }


    }

    "NOT for a NorthernIrelandCertifiedConsignor logged in trader" - {

      "must return true" - {
        "when the address has been provided" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers.set(ConsignorAddressPage, testUserAddress),
            ern = testNITemporaryCertifiedConsignorErn
          )

          ConsignorSection.isCompleted mustBe true
        }
      }
      "must return false" - {
        "when the address has NOT been provided" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers,
            ern = testNITemporaryCertifiedConsignorErn
          )

          ConsignorSection.isCompleted mustBe false
        }
      }
    }
  }
}
