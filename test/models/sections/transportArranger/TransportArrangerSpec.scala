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

package models.sections.transportArranger

import models.sections.transportArranger.TransportArranger._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class TransportArrangerSpec extends AnyFreeSpec with Matchers {

  "TransportArranger" - {
    "should have the correct enum mappings and audit descriptions" in {

      Consignor.toString mustBe "1"
      Consignor.auditDescription mustBe "Consignor"

      Consignee.toString mustBe "2"
      Consignee.auditDescription mustBe "Consignee"

      GoodsOwner.toString mustBe "3"
      GoodsOwner.auditDescription mustBe "GoodsOwner"

      Other.toString mustBe "4"
      Other.auditDescription mustBe "Other"
    }
  }
}
