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

import org.scalacheck.Arbitrary
import pages._
import pages.sections.consignee._
import pages.sections.journeyType.HowMovementTransportedPage

trait PageGenerators {

  implicit lazy val arbitraryConsigneeExportVatPage: Arbitrary[ConsigneeExportVatPage.type] =
    Arbitrary(ConsigneeExportVatPage)

  implicit lazy val arbitraryConsigneeExportPage: Arbitrary[ConsigneeExportPage.type] =
    Arbitrary(ConsigneeExportPage)

  implicit lazy val arbitraryConsigneeBusinessNamePage: Arbitrary[ConsigneeBusinessNamePage.type] =
    Arbitrary(ConsigneeBusinessNamePage)

  implicit lazy val arbitraryConsigneeAddressPage: Arbitrary[ConsigneeAddressPage.type] =
    Arbitrary(ConsigneeAddressPage)

  implicit lazy val arbitraryConsignorAddressPage: Arbitrary[ConsignorAddressPage.type] =
    Arbitrary(ConsignorAddressPage)

  implicit lazy val arbitraryLocalReferenceNumberPage: Arbitrary[LocalReferenceNumberPage.type] =
    Arbitrary(LocalReferenceNumberPage)

  implicit lazy val arbitraryDeferredMovementPage: Arbitrary[DeferredMovementPage.type] =
    Arbitrary(DeferredMovementPage)

  implicit lazy val arbitraryHowMovementTransportedPage: Arbitrary[HowMovementTransportedPage.type] =
    Arbitrary(HowMovementTransportedPage)
}
