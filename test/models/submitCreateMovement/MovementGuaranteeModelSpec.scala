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

import base.SpecBase
import fixtures.ItemFixtures
import models.VatNumberModel
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, UkTaxWarehouse}
import pages.sections.guarantor._
import pages.sections.info.DestinationTypePage
import pages.sections.items.ItemExciseProductCodePage
import play.api.test.FakeRequest

class MovementGuaranteeModelSpec extends SpecBase with ItemFixtures {
  "apply" - {
    "must return a MovementGuaranteeModel" - {
      "with NoGuarantorRequredUkoEu when no guarantor is required, and movement is uk to ue" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
            .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
            .set(GuarantorNamePage, "name")
            .set(GuarantorAddressPage, testUserAddress)
            .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat")))
            .set(DestinationTypePage, MovementScenario.EuTaxWarehouse),
          testNorthernIrelandErn
        )

        MovementGuaranteeModel.apply mustBe MovementGuaranteeModel(GuarantorArranger.NoGuarantorRequiredUkToEu, None)
      }

      "with NoGuarantorRequred when no guarantor is required, and movement is uk to uk" in {
        val drGb: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
            .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
            .set(GuarantorNamePage, "name")
            .set(GuarantorAddressPage, testUserAddress)
            .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat")))
            .set(DestinationTypePage, MovementScenario.UkTaxWarehouse.GB),
          testNorthernIrelandErn
        )

        MovementGuaranteeModel.apply(request = drGb) mustBe MovementGuaranteeModel(GuarantorArranger.NoGuarantorRequired, None)

        val drNi: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
            .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
            .set(GuarantorNamePage, "name")
            .set(GuarantorAddressPage, testUserAddress)
            .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat")))
            .set(DestinationTypePage, MovementScenario.UkTaxWarehouse.NI),
          testNorthernIrelandErn
        )

        MovementGuaranteeModel.apply(request = drNi) mustBe MovementGuaranteeModel(GuarantorArranger.NoGuarantorRequired, None)
      }

      "when guarantor is required" - {
        "because the user has answered that they want to provide one" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(GuarantorRequiredPage, true)
              .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
              .set(GuarantorNamePage, "name")
              .set(GuarantorAddressPage, testUserAddress)
              .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat")))
          )

          MovementGuaranteeModel.apply mustBe MovementGuaranteeModel(GuarantorArranger.GoodsOwner, Some(Seq(TraderModel(
            None,
            Some("name"),
            Some(AddressModel.fromUserAddress(testUserAddress)),
            Some("vat"),
            None
          ))))
        }

        "because the destination type and goods type requires one" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(DestinationTypePage, UkTaxWarehouse.GB)
              .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
              .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
              .set(GuarantorNamePage, "name")
              .set(GuarantorAddressPage, testUserAddress)
              .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat")))
          )

          MovementGuaranteeModel.apply mustBe MovementGuaranteeModel(GuarantorArranger.GoodsOwner, Some(Seq(TraderModel(
            None,
            Some("name"),
            Some(AddressModel.fromUserAddress(testUserAddress)),
            Some("vat"),
            None
          ))))
        }

        "because the destination is EU and goods type requires it" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(DestinationTypePage, EuTaxWarehouse)
              .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
              .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
              .set(GuarantorNamePage, "name")
              .set(GuarantorAddressPage, testUserAddress)
              .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat")))
          )

          MovementGuaranteeModel.apply mustBe MovementGuaranteeModel(GuarantorArranger.GoodsOwner, Some(Seq(TraderModel(
            None,
            Some("name"),
            Some(AddressModel.fromUserAddress(testUserAddress)),
            Some("vat"),
            None
          ))))
        }
      }
    }
    "must throw a mandatory page exception" - {
      "when guarantor is not required and no destination type has been answered" in {

        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
            .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
            .set(GuarantorNamePage, "name")
            .set(GuarantorAddressPage, testUserAddress)
            .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("vat"))),
          testNorthernIrelandErn
        )

        val exception = intercept[MissingMandatoryPage](MovementGuaranteeModel.apply)
        exception.message mustBe "Missing mandatory UserAnswer for page: 'destinationType'"
      }
    }
  }
}
