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

package pages.sections.sad

import base.SpecBase
import models.Index
import models.requests.DataRequest
import models.sections.sad.SadAddToListModel.NoMoreToCome
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, NotStarted}

class SadsSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when all items are finished" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ImportNumberPage(testIndex1), "")
              .set(ImportNumberPage(testIndex2), "")
              .set(SadAddToListPage, NoMoreToCome)
          )
        SadsSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when empty user answers" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        SadsSection.isCompleted mustBe false
      }
      "when there is somehow a sad with nothing in it" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(), emptyUserAnswers.copy(data = Json.obj(SadSection.toString -> JsArray(Seq(Json.obj())))))
        SadsSection.isCompleted mustBe false
      }
      "when at least one section is unfinished" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ImportNumberPage(testIndex1), "")
              .set(ImportNumberPage(testIndex2), "")
          )
        SadsSection.isCompleted mustBe false
      }
    }
  }

  "status" - {
    "must return completed" - {
      "when all sections are completed and add to list is no more" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ImportNumberPage(testIndex1), "")
              .set(ImportNumberPage(testIndex2), "")
              .set(SadAddToListPage, NoMoreToCome)
          )

        SadsSection.status mustBe Completed
      }

      "when max units added and all complete" in {
        val fullUserAnswers = (0 until 99).foldLeft(emptyUserAnswers)((answers, int) => answers
          .set(ImportNumberPage(Index(int)), "")
        )

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), fullUserAnswers)

        SadsSection.status mustBe Completed
      }
    }
    "must return not started" - {
      "when empty user answers" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        SadsSection.status mustBe NotStarted
      }
    }
  }
}
