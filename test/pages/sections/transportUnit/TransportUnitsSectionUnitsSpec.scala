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

  "onlyContainsOrIsEmpty()" - {

    "when TransportUnitCount is empty" - {

      "return true" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        TransportUnitsSectionUnits.onlyContainsOrIsEmpty(TransportUnitType.FixedTransport) mustBe true
      }
    }

    "when there is 1 TransportUnitType" - {

      "when the type matches the type checked for" - {

        "return true" in {
          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.FixedTransport)
          )
          TransportUnitsSectionUnits.onlyContainsOrIsEmpty(TransportUnitType.FixedTransport) mustBe true
        }
      }

      "when the type does NOT match the type checked for" - {

        "return true" in {
          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.Tractor)
          )
          TransportUnitsSectionUnits.onlyContainsOrIsEmpty(TransportUnitType.FixedTransport) mustBe false
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
          TransportUnitsSectionUnits.onlyContainsOrIsEmpty(TransportUnitType.FixedTransport) mustBe true
        }
      }

      "when the type matches the type given but other values exist" - {

        "return false" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.Tractor)
            .set(TransportUnitTypePage(1), TransportUnitType.FixedTransport)
            .set(TransportUnitTypePage(2), TransportUnitType.Tractor)
          )

          TransportUnitsSectionUnits.onlyContainsOrIsEmpty(TransportUnitType.FixedTransport) mustBe false
        }
      }

      "when the type does NOT match the type given" - {

        "return false" in {
          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(TransportUnitTypePage(0), TransportUnitType.Tractor)
            .set(TransportUnitTypePage(1), TransportUnitType.Tractor)
            .set(TransportUnitTypePage(2), TransportUnitType.Tractor)
          )
          TransportUnitsSectionUnits.onlyContainsOrIsEmpty(TransportUnitType.FixedTransport) mustBe false
        }
      }
    }
  }
}
