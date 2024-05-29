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

package models.sections.info.movementScenario

import models.sections.info.movementScenario.DestinationType._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class DestinationTypeSpec extends AnyFreeSpec with Matchers {

  "DestinationType" - {
    "should have the correct enum mappings and audit descriptions" in {

      TaxWarehouse.toString mustBe "1"
      TaxWarehouse.auditDescription mustBe "TaxWarehouse"

      RegisteredConsignee.toString mustBe "2"
      RegisteredConsignee.auditDescription mustBe "RegisteredConsignee"

      TemporaryRegisteredConsignee.toString mustBe "3"
      TemporaryRegisteredConsignee.auditDescription mustBe "TemporaryRegisteredConsignee"

      DirectDelivery.toString mustBe "4"
      DirectDelivery.auditDescription mustBe "DirectDelivery"

      ExemptedOrganisation.toString mustBe "5"
      ExemptedOrganisation.auditDescription mustBe "ExemptedOrganisation"

      Export.toString mustBe "6"
      Export.auditDescription mustBe "Export"

      UnknownDestination.toString mustBe "8"
      UnknownDestination.auditDescription mustBe "UnknownDestination"

      CertifiedConsignee.toString mustBe "9"
      CertifiedConsignee.auditDescription mustBe "CertifiedConsignee"

      TemporaryCertifiedConsignee.toString mustBe "10"
      TemporaryCertifiedConsignee.auditDescription mustBe "TemporaryCertifiedConsignee"

      ReturnToThePlaceOfDispatchOfTheConsignor.toString mustBe "11"
      ReturnToThePlaceOfDispatchOfTheConsignor.auditDescription mustBe "ReturnToThePlaceOfDispatchOfTheConsignor"
    }
  }
}
