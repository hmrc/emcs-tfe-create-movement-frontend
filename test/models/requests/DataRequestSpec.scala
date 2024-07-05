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

package models.requests

import base.SpecBase
import models._
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.test.FakeRequest

class DataRequestSpec extends SpecBase {
  "userTypeFromErn" - {
    Seq(
      ("GBRC123456789", GreatBritainRegisteredConsignor),
      ("XIRC123456789", NorthernIrelandRegisteredConsignor),
      ("GBWK123456789", GreatBritainWarehouseKeeper),
      ("XIWK123456789", NorthernIrelandWarehouseKeeper),
      ("XI00123456789", NorthernIrelandWarehouse),
      ("GB00123456789", GreatBritainWarehouse),
      ("XIPA000000001", NorthernIrelandCertifiedConsignor),
      ("XIPC000000001", NorthernIrelandTemporaryCertifiedConsignor),
      ("XI11123456789", Unknown)
    ).foreach {
      case (ern, userType) =>
        s"when provided ERN $ern" - {
          s"must return $userType" in {
            val fakeRequest = FakeRequest()
            val request = dataRequest(fakeRequest, ern = ern)

            request.userTypeFromErn mustBe userType
          }
        }
    }
  }

  "dispatchPlace" - {
    Seq(
      ("GB", Some(GreatBritain)),
      ("XI", Some(NorthernIreland)),
      ("beans", None)
    ).foreach {
      case (dp, res) =>
        s"when provided DISPATCH_PLACE $dp" - {
          s"must return $res" in {
            val request = dataRequest(
              request = FakeRequest(),
              answers = res match {
                case Some(dp) => emptyUserAnswers.set(DispatchPlacePage, dp)
                case None => emptyUserAnswers
              }
            )

            request.dispatchPlace mustBe res
          }
        }

    }
    "when no DISPATCH_PLACE is present" - {
      "must return None" in {
        val request = dataRequest(FakeRequest())
        request.dispatchPlace mustBe None
      }
    }
  }

  "isUkToUkAndNoGuarantor" - {
    "must return true" - {
      "when MovementType is UkToUk and GuarantorRequiredPage is false" in {
        Seq(
          (MovementScenario.UkTaxWarehouse.GB, "GB"),
          (MovementScenario.UkTaxWarehouse.NI, "XI"),
        ).foreach {
          case (scenario, prefix) =>
            val request = dataRequest(
              request = FakeRequest(),
              ern = s"${prefix}WK123456789",
              answers = emptyUserAnswers
                .set(DestinationTypePage, scenario)
                .set(GuarantorRequiredPage, false)
            )

            request.isUkToUkAndNoGuarantor mustBe true
        }
      }
    }

    "must return false" - {
      "when MovementType is UkToUk and GuarantorRequiredPage is true" in {
        Seq(
          (MovementScenario.UkTaxWarehouse.GB, "GB"),
          (MovementScenario.UkTaxWarehouse.NI, "XI"),
        ).foreach {
          case (scenario, prefix) =>
            val request = dataRequest(
              request = FakeRequest(),
              ern = s"${prefix}WK123456789",
              answers = emptyUserAnswers
                .set(DestinationTypePage, scenario)
                .set(GuarantorRequiredPage, true)
            )

            request.isUkToUkAndNoGuarantor mustBe false
        }
      }
      "when MovementType is not UkToUk" in {
        MovementScenario.values
          .filterNot(MovementScenario.UkTaxWarehouse.values.contains)
          .filterNot(MovementScenario.valuesForDutyPaidTraders.contains)
          .foreach {
            scenario =>
              val request = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, scenario)
                  .set(GuarantorRequiredPage, false)
              )

              request.isUkToUkAndNoGuarantor mustBe false
          }
        MovementScenario.valuesForDutyPaidTraders
          .foreach {
            scenario =>
              val request = dataRequest(
                request = FakeRequest(),
                ern = "XIPA000000001",
                answers = emptyUserAnswers
                  .set(DestinationTypePage, scenario)
                  .set(GuarantorRequiredPage, false)
              )

              request.isUkToUkAndNoGuarantor mustBe false
          }
      }
    }
  }

  "isUkToEuAndNoGuarantor" - {
    val ukToEuMovementScenarios: Seq[MovementScenario] = Seq(
      MovementScenario.DirectDelivery,
      MovementScenario.EuTaxWarehouse,
      MovementScenario.ExemptedOrganisation,
      MovementScenario.RegisteredConsignee,
      MovementScenario.UnknownDestination
    )
    "must return true" - {
      "when MovementType is UkToEu and GuarantorRequiredPage is false" in {
        ukToEuMovementScenarios.foreach {
          scenario =>
            val request = dataRequest(
              request = FakeRequest(),
              ern = "GBWK123456789",
              answers = emptyUserAnswers
                .set(DestinationTypePage, scenario)
                .set(GuarantorRequiredPage, false)
            )

            request.isUkToEuAndNoGuarantor mustBe true
        }
        MovementScenario.valuesForDutyPaidTraders.foreach {
          scenario =>
            val request = dataRequest(
              request = FakeRequest(),
              ern = "XIPA123456789",
              answers = emptyUserAnswers
                .set(DestinationTypePage, scenario)
                .set(GuarantorRequiredPage, false)
            )

            request.isUkToEuAndNoGuarantor mustBe true
        }
      }
    }

    "must return false" - {
      "when MovementType is UkToEu and GuarantorRequiredPage is true" in {
        ukToEuMovementScenarios.foreach {
          scenario =>
            val request = dataRequest(
              request = FakeRequest(),
              ern = "GBWK123456789",
              answers = emptyUserAnswers
                .set(DestinationTypePage, scenario)
                .set(GuarantorRequiredPage, true)
            )

            request.isUkToEuAndNoGuarantor mustBe false
        }
      }
      "when MovementType is not UkToEu" in {
        MovementScenario.values
          .filterNot(ukToEuMovementScenarios.contains)
          .filterNot(MovementScenario.valuesForDutyPaidTraders.contains)
          .foreach {
            scenario =>
              val request = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, scenario)
                  .set(GuarantorRequiredPage, false)
              )

              request.isUkToEuAndNoGuarantor mustBe false
          }
      }
    }
  }

  "isUnknownDestination" - {
    "must return true" - {
      "when DestinationTypePage is UnknownDestination" in {
        val request = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, MovementScenario.UnknownDestination)
        )

        request.isUnknownDestination mustBe true
      }
    }
    "must return false" - {
      "when DestinationTypePage is not UnknownDestination" in {
        MovementScenario.values
          .filterNot(_ == MovementScenario.UnknownDestination)
          .foreach {
            scenario =>
              val request = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers.set(DestinationTypePage, scenario)
              )

              request.isUnknownDestination mustBe false
          }
      }
    }
  }
}
