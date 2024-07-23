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

package pages.sections.info

import base.SpecBase
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DestinationTypePageSpec extends SpecBase {

  val niValues = Seq(
    DirectDelivery,
    ExemptedOrganisation,
    UnknownDestination,
    RegisteredConsignee,
    EuTaxWarehouse,
    TemporaryRegisteredConsignee,
    CertifiedConsignee,
    TemporaryCertifiedConsignee
  )

  ".isNItoEU" - {

    "when request is NI" - {

      values.foreach { destination =>

        val answers = emptyUserAnswers.set(DestinationTypePage, destination)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), answers, testNorthernIrelandErn)

        s"when destination type is '$destination'" - {
          if(niValues.contains(destination)) {
            "must return true" in {
              DestinationTypePage.isNItoEuMovement mustBe true
            }
          } else {
            "must return false" in {
              DestinationTypePage.isNItoEuMovement mustBe false
            }
          }
        }
      }
    }

    "when request is NOT NI" - {

      values.foreach { destination =>

        val answers = emptyUserAnswers.set(DestinationTypePage, destination)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), answers, testGreatBritainErn)

        s"when destination type is '$destination'" - {
          "must return false" in {
            DestinationTypePage.isNItoEuMovement mustBe false
          }
        }
      }
    }
  }
}
