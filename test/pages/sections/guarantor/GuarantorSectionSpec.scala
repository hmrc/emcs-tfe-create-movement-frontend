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

package pages.sections.guarantor

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner}
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeBusinessNamePage, ConsigneeExportPage, ConsigneeExportVatPage}
import pages.sections.consignor.ConsignorAddressPage
import play.api.test.FakeRequest

class GuarantorSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when Consignor is selected and completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, Consignor)
            .set(ConsignorAddressPage, UserAddress(None, "", "", ""))
        )
        GuarantorSection.isCompleted mustBe true
      }
      "when Consignee is selected and completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, Consignee)
            .set(ConsigneeExportPage, true)
            .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.No, None, None))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        GuarantorSection.isCompleted mustBe true
      }
      "when guarantor is not required" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
        )
        GuarantorSection.isCompleted mustBe true
      }
      "when another option is selected and the rest of the Guarantor section is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, GoodsOwner)
            .set(GuarantorNamePage, "")
            .set(GuarantorVatPage, "")
            .set(GuarantorAddressPage, UserAddress(None, "", "", ""))
        )
        GuarantorSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when Consignor is selected and not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, Consignor)
        )
        GuarantorSection.isCompleted mustBe false
      }
      "when Consignee is selected and not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, Consignee)
        )
        GuarantorSection.isCompleted mustBe false
      }
      "when only GuarantorRequiredPage is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
        )
        GuarantorSection.isCompleted mustBe false
      }
      "when nothing is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
        )
        GuarantorSection.isCompleted mustBe false
      }
      "when another option is selected and the rest of the Guarantor section is not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, GoodsOwner)
            .set(GuarantorNamePage, "")
            .set(GuarantorVatPage, "")
        )
        GuarantorSection.isCompleted mustBe false
      }
    }
  }
}
