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
import fixtures.MovementSubmissionFailureFixtures
import play.api.test.FakeRequest
import utils.SubmissionFailureErrorCodes.localReferenceNumberError

class LocalReferenceNumberPageSpec extends SpecBase with MovementSubmissionFailureFixtures {

  val page = LocalReferenceNumberPage()

  "when calling isSubmissionErrorOnPage" - {

    "must return true" - {

      s"when the error is $localReferenceNumberError and not fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasBeenFixed = false))
        ))) mustBe true
      }
    }

    "must return false" - {

      s"when the error is not a $localReferenceNumberError" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "4403", hasBeenFixed = false))
        ))) mustBe false
      }

      s"when the error is $localReferenceNumberError but fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasBeenFixed = true))
        ))) mustBe false
      }

      "no errors exist" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe false
      }
    }
  }

  "when calling getOriginalAttributeValueForPage" - {

    "must return Some(_)" - {

      s"when the error type is $localReferenceNumberError and an original attribute value exists" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasBeenFixed = false, originalAttributeValue = Some("LRN1")))
        ))) mustBe Some("LRN1")
      }
    }

    "must return None" - {

      s"when the error type is not $localReferenceNumberError" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "4403", hasBeenFixed = false, originalAttributeValue = Some("LRN1")))
        ))) mustBe None
      }

      "when the original value is not defined" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasBeenFixed = false, originalAttributeValue = None))
        ))) mustBe None
      }
    }
  }

}
