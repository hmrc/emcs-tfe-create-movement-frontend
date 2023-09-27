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

package generators

import fixtures.UserAddressFixtures
import models.UserAddress
import models.sections.consignee.ConsigneeExportVat
import models.sections.consignee.ConsigneeExportVatType.{No, YesEoriNumber, YesVatNumber}
import models.sections.journeyType.HowMovementTransported
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators extends UserAddressFixtures {

  implicit lazy val arbitraryConsigneeExportVat: Arbitrary[ConsigneeExportVat] = {
    Arbitrary {
      Gen.oneOf(
        ConsigneeExportVat(YesVatNumber, Some("vat123"), None),
        ConsigneeExportVat(YesEoriNumber, None, Some("eori123")),
        ConsigneeExportVat(No, None, None)
      )
    }
  }

  implicit lazy val arbitraryUserAddress: Arbitrary[UserAddress] =
    Arbitrary {
      Gen.oneOf(Set(userAddressModelMax))
    }

  implicit lazy val arbitraryHowMovementTransported: Arbitrary[HowMovementTransported] =
    Arbitrary {
      Gen.oneOf(HowMovementTransported.values)
    }

}
