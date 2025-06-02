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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages.English
import models.VatNumberModel
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{DirectDelivery, UkTaxWarehouse}
import models.sections.journeyType.HowMovementTransported.{FixedTransportInstallations, RoadTransport}
import org.scalamock.scalatest.MockFactory
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.guarantor._
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.CardViewModel

class GuarantorCheckAnswersHelperSpec extends SpecBase with MockFactory {
  trait Test {
    implicit val msgs: Messages = messages(Seq(English.lang))
    val helper = new GuarantorCheckAnswersHelper()
  }

  "summaryList" - {

    "when movement is NItoEU and JourneyType is FixedTransportInstallations (or JourneyType has not been answered yet)" - {

      GuarantorArranger.displayValues.foreach {
        case value@(GoodsOwner | Transporter) =>

          "must render six rows (GuarantorRequired is included)" - {
            s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
              implicit val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(HowMovementTransportedPage, FixedTransportInstallations)
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)
                  .set(GuarantorVatPage, VatNumberModel(true, Some("gurantor123")))
                  .set(GuarantorAddressPage, testUserAddress),
                testNorthernIrelandErn
              )
              helper.summaryList()(request, msgs).rows.length mustBe 5
            }
          }
        case value =>
          "must render five rows (GuarantorRequired is included)" - {
            s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
              implicit val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)
                  .set(ConsigneeAddressPage, testUserAddress),
                testNorthernIrelandErn
              )
              helper.summaryList()(request, msgs).rows.length mustBe 4
            }
          }
      }
    }

    "when movement is NItoEU and JourneyType is anything other than FixedTransportInstallations" - {

      GuarantorArranger.displayValues.foreach {
        case value@(GoodsOwner | Transporter) =>

          "must render five rows (GuarantorRequired is excluded as it must be true and can't be changed)" - {
            s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
              implicit val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(HowMovementTransportedPage, RoadTransport)
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)
                  .set(GuarantorVatPage, VatNumberModel(true, Some("gurantor123")))
                  .set(GuarantorAddressPage, testUserAddress),
                testNorthernIrelandErn
              )
              helper.summaryList()(request, msgs).rows.length mustBe 4
            }
          }
        case value =>
          "must render four rows (GuarantorRequired is excluded as it must be true and can't be changed)" - {
            s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
              implicit val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(HowMovementTransportedPage, RoadTransport)
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)
                  .set(ConsigneeAddressPage, testUserAddress),
                testNorthernIrelandErn
              )
              helper.summaryList()(request, msgs).rows.length mustBe 3
            }
          }
      }
    }

    "when movement is NOT UKtoEU" - {

      GuarantorArranger.displayValues.foreach {
        case value@(GoodsOwner | Transporter) =>

          "must render six rows" - {
            s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
              implicit val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)
                  .set(GuarantorVatPage, VatNumberModel(true, Some("gurantor123")))
                  .set(GuarantorAddressPage, testUserAddress),
                testGreatBritainWarehouseKeeperErn
              )
              helper.summaryList()(request, msgs).rows.length mustBe 5
            }
          }
        case value =>
          "must render five rows" - {
            s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
              implicit val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)
                  .set(ConsigneeAddressPage, testUserAddress),
                testGreatBritainWarehouseKeeperErn
              )
              helper.summaryList()(request, msgs).rows.length mustBe 4
            }
          }
      }

      "must render one row" - {
        "when no answers for the guarantor section" in new Test {
          implicit val request: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(DestinationTypePage, UkTaxWarehouse.GB)
              .set(GuarantorRequiredPage, false),
            testGreatBritainWarehouseKeeperErn
          )
          helper.summaryList()(request, msgs).rows.length mustBe 1
        }
      }

      "must render as card layout" - {
        "when asCard is 'true'" in new Test {
          implicit val request: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(DestinationTypePage, UkTaxWarehouse.GB)
              .set(GuarantorRequiredPage, false),
            testGreatBritainWarehouseKeeperErn
          )
          helper.summaryList(asCard = true)(request, msgs) mustBe SummaryList(
            rows = Seq(
              GuarantorRequiredSummary.row(),
              GuarantorArrangerSummary.row(),
              GuarantorErnVatSummary.rows(),
              GuarantorAddressSummary.row(),
            ).flatten,
            card = Some(CardViewModel(
              title = "Guarantor",
              headingLevel = 2,
              actions = None
            ))
          )
        }
      }
    }
  }
}
