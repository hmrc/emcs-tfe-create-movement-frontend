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
import models.sections.info.movementScenario.MovementType.UkToEu
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

  "isUKtoEUMovement" - {

    "when NI ern" - {

      "when not Duty Paid" - {

        implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers, testNorthernIrelandErn)

        MovementScenario.valuesEu.collect {
          case scenario if scenario.movementType == UkToEu =>
            val request = dr.copy(userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))

            s"must return true when DestinationType is '$scenario'" in {
              request.isUkToEuMovement mustBe true
            }
          case scenario =>
            val request = dr.copy(userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))

            s"must return false when DestinationType is '$scenario'" in {
              request.isUkToEuMovement mustBe false
            }
        }
      }

      "when Duty Paid" - {

        implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers, testNIDutyPaidErn)

        MovementScenario.valuesForDutyPaidTraders.collect {
          case scenario if scenario.movementType == UkToEu =>
            val request = dr.copy(userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))

            s"must return true when DestinationType is '$scenario'" in {
              request.isUkToEuMovement mustBe true
            }
          case scenario =>
            val request = dr.copy(userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))

            s"must return false when DestinationType is '$scenario'" in {
              request.isUkToEuMovement mustBe false
            }
        }
      }
    }

    "when GB ern" - {

      implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers, testGreatBritainErn)

      MovementScenario.valuesExportUkAndUkTaxWarehouse.foreach { scenario =>

        val request = dr.copy(userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))

        s"must return false when DestinationType is '$scenario'" in {
          request.isUkToEuMovement mustBe false
        }
      }
    }
  }
}
