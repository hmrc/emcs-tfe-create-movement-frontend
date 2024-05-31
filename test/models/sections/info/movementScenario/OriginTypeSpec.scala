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

package models.sections.info.movementScenario

import models.sections.info.movementScenario.OriginType._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class OriginTypeSpec extends AnyFreeSpec with Matchers {

  "OriginType" - {
    "should have the correct enum mappings and audit descriptions" in {

      TaxWarehouse.toString mustBe "1"
      TaxWarehouse.auditDescription mustBe "TaxWarehouse"

      Imports.toString mustBe "2"
      Imports.auditDescription mustBe "Imports"

      DutyPaid.toString mustBe "3"
      DutyPaid.auditDescription mustBe "DutyPaid"
    }
  }
}
