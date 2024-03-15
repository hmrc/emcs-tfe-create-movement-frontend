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

package pages.sections.items

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import play.api.test.FakeRequest
import utils.ItemDegreesPlatoError

class ItemDegreesPlatoPageSpec extends SpecBase with MovementSubmissionFailureFixtures {

  val page: ItemDegreesPlatoPage = ItemDegreesPlatoPage(testIndex2)

  "when calling isSubmissionErrorOnPage" - {

    "must return true" - {

      s"when the error is ${ItemDegreesPlatoError.code} and not fixed (at the specified index)" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2))
        ))) mustBe true
      }
    }

    "must return false" - {

      s"when the error at the index is not a ${ItemDegreesPlatoError.code}" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(errorType = "0000"))
        ))) mustBe false
      }

      s"when the error at the index is ${ItemDegreesPlatoError.code} but fixed" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(hasBeenFixed = true))
        ))) mustBe false
      }

      "when the error exists but not at the specified index" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(1))
        ))) mustBe false
      }

      "when the error has no error location" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(errorLocation = None))
        ))) mustBe false
      }

      "no errors exist" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe false
      }
    }
  }

  "when calling getOriginalAttributeValueForPage" - {

    "must return Some(_)" - {

      s"when the error type is ${ItemDegreesPlatoError.code} and an original attribute value exists (at the specified index)" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2))
        ))) mustBe Some("10")
      }
    }

    "must return None" - {

      s"when the error at the index is not ${ItemDegreesPlatoError.code}" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(errorType = "0000"))
        ))) mustBe None
      }

      "when the error exists but not at the specified index" in {
        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(1))
        ))) mustBe None
      }

      "when the error has no error location" in {
        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(errorLocation = None))
        ))) mustBe None
      }

      "when the original value is not defined" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(originalAttributeValue = None))
        ))) mustBe None
      }
    }
  }

  "when calling indexesOfMovementSubmissionErrors" - {

    "must return Seq(-1)" - {

      s"when the ${ItemDegreesPlatoError.code} error type does not exist in the submission failures" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "0001", hasBeenFixed = false, originalAttributeValue = None))
        ))) mustBe Seq(-1)
      }

      "when the error has no error location" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(2).copy(errorLocation = None))
        ))) mustBe Seq(-1)
      }

      "when the error exists but not at the specified index" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemDegreesPlatoFailure(1))
        ))) mustBe Seq(-1)
      }

    }

    "must return Seq(<index>)" - {

      s"when the ${ItemDegreesPlatoError.code} error type exists in the submission failures (at the specified index)" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            itemDegreesPlatoFailure(1),
            itemDegreesPlatoFailure(2),
            movementSubmissionFailure
          )))) mustBe Seq(1)
      }
    }
  }

  "when calling getSubmissionErrorCode" - {

    "should return Some" - {

      Seq(true, false).foreach { isOnAddToList =>

        s"when isOnAddToList = $isOnAddToList" - {

          "and a submission failure exists at this index" in {

            page.getSubmissionErrorCode(isOnAddToList)(dataRequest(FakeRequest(), emptyUserAnswers.copy(
              submissionFailures = Seq(
                itemDegreesPlatoFailure(1),
                itemDegreesPlatoFailure(2),
                movementSubmissionFailure
              )))) mustBe Some(ItemDegreesPlatoError(testIndex2, isOnAddToList))
          }
        }
      }
    }

    "should return None" - {

      "when no submission failures exist" in {

        page.getSubmissionErrorCode(isOnAddToList = false)(dataRequest(FakeRequest(), emptyUserAnswers.copy(submissionFailures = Seq.empty))) mustBe None
      }

      "when no degrees plato submission failures exist" in {

        page.getSubmissionErrorCode(isOnAddToList = false)(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            movementSubmissionFailure
          )))) mustBe None
      }

      "when a degrees plato submission failure exists but at a different index" in {

        page.getSubmissionErrorCode(isOnAddToList = false)(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            itemDegreesPlatoFailure(1),
            movementSubmissionFailure
          )))) mustBe None
      }
    }
  }

}
