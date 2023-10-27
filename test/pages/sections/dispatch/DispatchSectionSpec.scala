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

package pages.sections.dispatch

import base.SpecBase
import models.requests.DataRequest
import play.api.test.FakeRequest

class DispatchSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      // TODO: Update when CAM-DIS06 is built
      "when finished" ignore {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        DispatchSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      // TODO: Update when CAM-DIS06 is built
      "when not finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        DispatchSection.isCompleted mustBe false
      }
    }
  }
}