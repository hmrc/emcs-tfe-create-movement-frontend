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

package pages.sections.exportInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import models.requests.DataRequest
import play.api.test.FakeRequest
import utils.SubmissionFailureErrorCodes.ExportCustomsOfficeNumberError
import viewmodels.taskList.{NotStarted, UpdateNeeded}

class ExportInformationSectionSpec extends SpecBase with MovementSubmissionFailureFixtures {
  "isCompleted" - {
    "must return true" - {
      "when finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ExportCustomsOfficePage, ""))
        ExportInformationSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when not finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ExportInformationSection.isCompleted mustBe false
      }
    }
  }

  "status" - {

    s"must return $UpdateNeeded" - {

      "when a 704 error exists in this section" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false)))
        )
        ExportInformationSection.status mustBe UpdateNeeded
      }
    }

    s"must return $NotStarted" - {

      s"when there is no answer for $ExportCustomsOfficePage" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ExportInformationSection.status mustBe NotStarted
      }
    }
  }
}
