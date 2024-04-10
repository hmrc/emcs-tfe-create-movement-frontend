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

package pages.sections.importInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import models.requests.DataRequest
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, NotStarted, UpdateNeeded}

class ImportInformationSectionSpec extends SpecBase with MovementSubmissionFailureFixtures {

  "isCompleted" - {
    "must return true" - {
      "when finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ImportCustomsOfficeCodePage, ""))
        ImportInformationSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when not finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ImportInformationSection.isCompleted mustBe false
      }
    }
  }

  "status" - {
    "must return UpdateNeeded" - {
      "when a 704 error exists and has NOT been fixed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(importCustomsOfficeCodeFailure)
        ))
        ImportInformationSection.status mustBe UpdateNeeded
      }
    }

    "must return Complete" - {
      "when a 704 error exists that has been fixed" in {
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(ImportCustomsOfficeCodePage, testGBImportCustomsOffice)
              .copy(submissionFailures = Seq(importCustomsOfficeCodeFailure.copy(hasBeenFixed = true))
          ))
          ImportInformationSection.status mustBe Completed
      }
    }

    "must return NotStarted" - {
      "when no answer exists" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ImportInformationSection.status mustBe NotStarted
      }
    }
  }
}
