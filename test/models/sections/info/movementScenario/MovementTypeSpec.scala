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

import models.sections.info.movementScenario.MovementType._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MovementTypeSpec extends AnyFreeSpec with Matchers {

  "MovementType" - {
    "should have the correct enum mappings and audit descriptions" in {

      UkToUk.toString mustBe "1"
      UkToUk.auditDescription mustBe "UkToUk"

      UkToEu.toString mustBe "2"
      UkToEu.auditDescription mustBe "UkToEu"

      DirectExport.toString mustBe "3"
      DirectExport.auditDescription mustBe "DirectExport"

      ImportEu.toString mustBe "4"
      ImportEu.auditDescription mustBe "ImportEu"

      ImportUk.toString mustBe "5"
      ImportUk.auditDescription mustBe "ImportUk"

      IndirectExport.toString mustBe "6"
      IndirectExport.auditDescription mustBe "IndirectExport"

      ImportDirectExport.toString mustBe "7"
      ImportDirectExport.auditDescription mustBe "ImportDirectExport"

      ImportIndirectExport.toString mustBe "8"
      ImportIndirectExport.auditDescription mustBe "ImportIndirectExport"

      ImportUnknownDestination.toString mustBe "9"
      ImportUnknownDestination.auditDescription mustBe "ImportUnknownDestination"
    }
  }
}
