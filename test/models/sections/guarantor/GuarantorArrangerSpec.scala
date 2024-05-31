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

package models.sections.guarantor

import models.sections.guarantor.GuarantorArranger._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class GuarantorArrangerSpec extends AnyFreeSpec with Matchers {

  "GuarantorArranger" - {
    "should have the correct enum mappings and audit descriptions" in {

      Consignor.toString mustBe "1"
      Consignor.auditDescription mustBe "Consignor"

      Consignee.toString mustBe "4"
      Consignee.auditDescription mustBe "Consignee"

      GoodsOwner.toString mustBe "3"
      GoodsOwner.auditDescription mustBe "GoodsOwner"

      Transporter.toString mustBe "2"
      Transporter.auditDescription mustBe "Transporter"

      NoGuarantorRequiredUkToEu.toString mustBe "5"
      NoGuarantorRequiredUkToEu.auditDescription mustBe "NoGuarantorRequiredUkToEu"

      NoGuarantorRequired.toString mustBe "0"
      NoGuarantorRequired.auditDescription mustBe "NoGuarantorRequired"
    }
  }
}
