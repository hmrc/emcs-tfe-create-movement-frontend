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

package pages.sections.importInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import play.api.test.FakeRequest
import utils.SubmissionFailureErrorCodes.importCustomsOfficeCodeError

class ImportCustomsOfficeCodePageSpec extends SpecBase with MovementSubmissionFailureFixtures {

  val page = ImportCustomsOfficeCodePage

  "when calling isSubmissionErrorOnPage" - {

    "must return true" - {

      s"when the error is $importCustomsOfficeCodeError and not fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(importCustomsOfficeCodeFailure)
        ))) mustBe true
      }
    }

    "must return false" - {

      s"when the error is not a $importCustomsOfficeCodeError" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure)
        ))) mustBe false
      }

      s"when the error is $importCustomsOfficeCodeError but fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(importCustomsOfficeCodeFailure.copy(hasBeenFixed = true))
        ))) mustBe false
      }

      "no errors exist" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe false
      }
    }
  }

  "when calling getOriginalAttributeValueForPage" - {

    "must return Some(_)" - {

      s"when the error type is $importCustomsOfficeCodeError and an original attribute value exists" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(importCustomsOfficeCodeFailure)
        ))) mustBe Some(testImportCustomsOffice)
      }
    }

    "must return None" - {

      s"when the error type is not $importCustomsOfficeCodeError" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure)
        ))) mustBe None
      }

      "when the original value is not defined" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(importCustomsOfficeCodeFailure.copy(originalAttributeValue = None))
        ))) mustBe None
      }
    }
  }

  "when calling indexesOfMovementSubmissionErrors" - {

    "must return Seq(-1)" - {

      s"when the $importCustomsOfficeCodeError error type does not exist in the submission failures" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure)
        ))) mustBe Seq(-1)
      }
    }

    "must return Seq(<index>)" - {

      s"when the $importCustomsOfficeCodeError error type exists in the submission failures" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure, importCustomsOfficeCodeFailure)
        ))) mustBe Seq(1)
      }
    }
  }

}
