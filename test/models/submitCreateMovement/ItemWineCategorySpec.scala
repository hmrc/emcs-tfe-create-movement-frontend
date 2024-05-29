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

package models.submitCreateMovement

import models.submitCreateMovement.ItemWineCategory._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ItemWineCategorySpec extends AnyFreeSpec with Matchers {

  "ItemWineCategory" - {
    "should have the correct enum mappings and audit descriptions" in {

      EuWineWithoutPdoOrPgi.toString mustBe "1"
      EuWineWithoutPdoOrPgi.auditDescription mustBe "EuWineWithoutPdoOrPgi"

      EuVarietalWineWithoutPdoOrPgi.toString mustBe "2"
      EuVarietalWineWithoutPdoOrPgi.auditDescription mustBe "EuVarietalWineWithoutPdoOrPgi"

      EuWineWithPdoOrPgiOrGi.toString mustBe "3"
      EuWineWithPdoOrPgiOrGi.auditDescription mustBe "EuWineWithPdoOrPgiOrGi"

      ImportedWine.toString mustBe "4"
      ImportedWine.auditDescription mustBe "ImportedWine"

      Other.toString mustBe "5"
      Other.auditDescription mustBe "Other"
    }
  }
}
