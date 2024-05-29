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

import models.sections.items.ItemWineGrowingZone._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ItemWineGrowingZoneSpec extends AnyFreeSpec with Matchers {

  "ItemWineGrowingZone" - {
    "should have the correct enum mappings and audit descriptions" in {

      A.toString mustBe "1"
      A.auditDescription mustBe "ZoneA"

      B.toString mustBe "2"
      B.auditDescription mustBe "ZoneB"

      CI.toString mustBe "3"
      CI.auditDescription mustBe "ZoneCI"

      CII.toString mustBe "4"
      CII.auditDescription mustBe "ZoneCII"

      CIII_A.toString mustBe "5"
      CIII_A.auditDescription mustBe "ZoneCIII_A"

      CIII_B.toString mustBe "6"
      CIII_B.auditDescription mustBe "ZoneCIII_B"
    }
  }
}
