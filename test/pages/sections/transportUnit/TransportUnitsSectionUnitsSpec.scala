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

package pages.sections.transportUnit

import base.SpecBase
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import play.api.test.FakeRequest

class TransportUnitsSectionUnitsSpec extends SpecBase {

  "containsTransportUnitType" - {

    "when TransportUnitCount is empty" - {

      "return None" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        val expectedResult = None

        val actualResult = TransportUnitsSectionUnits.containsTransportUnitType(TransportUnitType.FixedTransport)

        actualResult mustBe expectedResult
      }
    }

    "when there is 1 TransportUnitType" - {

      "when the type matches the type checked for" - {

        "return true" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.FixedTransport)
          )

          val expectedResult = Some(true)

          val actualResult = TransportUnitsSectionUnits.containsTransportUnitType(TransportUnitType.FixedTransport)

          actualResult mustBe expectedResult
        }
      }

      "when the type does NOT match the type checked for" - {

        "return true" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.Tractor)
          )

          val expectedResult = Some(false)

          val actualResult = TransportUnitsSectionUnits.containsTransportUnitType(TransportUnitType.FixedTransport)

          actualResult mustBe expectedResult
        }
      }
    }

    "when there is multiple TransportUnitTypes" - {

      "when the all types match the type checked for" - {

        "return true" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.FixedTransport)
            .set(TransportUnitTypePage(1), TransportUnitType.FixedTransport)
            .set(TransportUnitTypePage(2), TransportUnitType.FixedTransport)
          )

          val expectedResult = Some(true)

          val actualResult = TransportUnitsSectionUnits.containsTransportUnitType(TransportUnitType.FixedTransport)

          actualResult mustBe expectedResult
        }
      }

      "when the type matches the type given" - {

        "return true" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.Tractor)
            .set(TransportUnitTypePage(1), TransportUnitType.FixedTransport)
            .set(TransportUnitTypePage(2), TransportUnitType.Tractor)
          )

          val expectedResult = Some(true)

          val actualResult = TransportUnitsSectionUnits.containsTransportUnitType(TransportUnitType.FixedTransport)

          actualResult mustBe expectedResult
        }
      }

      "when the type does NOT match the type given" - {

        "return true" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.Tractor)
            .set(TransportUnitTypePage(1), TransportUnitType.Tractor)
            .set(TransportUnitTypePage(2), TransportUnitType.Tractor)
          )

          val expectedResult = Some(false)

          val actualResult = TransportUnitsSectionUnits.containsTransportUnitType(TransportUnitType.FixedTransport)

          actualResult mustBe expectedResult
        }
      }
    }
  }
}
