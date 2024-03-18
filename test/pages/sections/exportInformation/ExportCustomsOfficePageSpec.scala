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

package pages.sections.exportInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import play.api.test.FakeRequest
import utils.ExportCustomsOfficeNumberError

class ExportCustomsOfficePageSpec extends SpecBase with MovementSubmissionFailureFixtures {

  val page = ExportCustomsOfficePage

  "when calling isSubmissionErrorOnPage" - {

    "must return true" - {

      s"when the error is ${ExportCustomsOfficeNumberError.code} and not fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false))
        ))) mustBe true
      }
    }

    "must return false" - {

      s"when the error is not a ${ExportCustomsOfficeNumberError.code}" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "4403", hasBeenFixed = false))
        ))) mustBe false
      }

      s"when the error is ${ExportCustomsOfficeNumberError.code} but fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = true))
        ))) mustBe false
      }

      "no errors exist" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe false
      }
    }
  }

  "when calling getOriginalAttributeValueForPage" - {

    "must return Some(_)" - {

      s"when the error type is ${ExportCustomsOfficeNumberError.code} and an original attribute value exists" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false, originalAttributeValue = Some("LRN1")))
        ))) mustBe Some("LRN1")
      }
    }

    "must return None" - {

      s"when the error type is not ${ExportCustomsOfficeNumberError.code}" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "4403", hasBeenFixed = false, originalAttributeValue = Some("LRN1")))
        ))) mustBe None
      }

      "when the original value is not defined" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false, originalAttributeValue = None))
        ))) mustBe None
      }
    }
  }

  "when calling indexesOfMovementSubmissionErrors" - {

    "must return Seq.empty" - {

      s"when the ${ExportCustomsOfficeNumberError.code} error type does not exist in the submission failures" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "0001", hasBeenFixed = false, originalAttributeValue = None))
        ))) mustBe Seq.empty
      }
    }

    "must return Seq(<index>)" - {

      s"when the ${ExportCustomsOfficeNumberError.code} error type exists in the submission failures" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            movementSubmissionFailure.copy(errorType = "0001", hasBeenFixed = false, originalAttributeValue = None),
            movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false, originalAttributeValue = None)
          )))) mustBe Seq(1)
      }
    }
  }
}
