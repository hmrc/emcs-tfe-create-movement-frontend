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

package pages.sections.transportArranger

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import models.sections.consignee._
import models.sections.transportArranger.TransportArranger._
import pages.sections.consignee._
import pages.sections.consignor._
import play.api.test.FakeRequest

class TransportArrangerSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when Consignor is selected and completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Consignor)
            .set(ConsignorAddressPage, UserAddress(None, "", "", ""))
        )
        TransportArrangerSection.isCompleted mustBe true
      }
      "when Consignee is selected and completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Consignee)
            .set(ConsigneeExportPage, true)
            .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.No, None, None))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        TransportArrangerSection.isCompleted mustBe true
      }
      "when another option is selected and the rest of the TransportArranger section is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Other)
            .set(TransportArrangerNamePage, "")
            .set(TransportArrangerVatPage, "")
            .set(TransportArrangerAddressPage, UserAddress(None, "", "", ""))
        )
        TransportArrangerSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when Consignor is selected and not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Consignor)
        )
        TransportArrangerSection.isCompleted mustBe false
      }
      "when Consignee is selected and not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Consignee)
        )
        TransportArrangerSection.isCompleted mustBe false
      }
      "when nothing is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
        )
        TransportArrangerSection.isCompleted mustBe false
      }
      "when another option is selected and the rest of the TransportArranger section is not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Other)
        )
        TransportArrangerSection.isCompleted mustBe false
      }
    }
  }
}
