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
import models.sections.transportUnit.TransportUnitType.Container
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest

class TransportUnitsSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when all items are finished" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportSealChoicePage(testIndex2), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), false)
          )
        TransportUnitsSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when empty user answers" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        TransportUnitsSection.isCompleted mustBe false
      }
      "when there is somehow a transport unit with nothing in it" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(), emptyUserAnswers.copy(data = Json.obj(TransportUnitsSection.toString -> JsArray(Seq(Json.obj())))))
        TransportUnitsSection.isCompleted mustBe false
      }
      "when at least one section is unfinished" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportSealChoicePage(testIndex2), false)
          )
        TransportUnitsSection.isCompleted mustBe false
      }
    }
  }
}
