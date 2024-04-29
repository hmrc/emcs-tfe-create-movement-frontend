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

package pages.sections.documents

import base.SpecBase
import fixtures.DocumentTypeFixtures
import models.requests.DataRequest
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, InProgress, NotStarted}

class DocumentSectionSpec extends SpecBase with DocumentTypeFixtures {

  "status" - {

    "return Complete" - {

      "when DocumentType has been answered" - {

        "when DocumentReferencePage has been answered" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DocumentTypePage(0), documentTypeModel)
            .set(DocumentReferencePage(0), "reference")
          )

          DocumentSection(0).status mustBe Completed
        }
      }
    }

    "return NotStarted" - {

      "when DocumentTypePage and DocumentReferencePage have NOT been answered" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        DocumentSection(0).status mustBe NotStarted
      }
    }

    "return InProgress" - {

      "when only ONE question has been answered" - {

        "for DocumentTypePage" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DocumentTypePage(0), documentTypeModel)
          )

          DocumentSection(0).status mustBe InProgress
        }

        "for DocumentReferencePage" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DocumentReferencePage(0), "reference")
          )

          DocumentSection(0).status mustBe InProgress
        }

      }
    }
  }
}
