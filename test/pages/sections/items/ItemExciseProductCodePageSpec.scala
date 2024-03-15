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
import utils.SubmissionFailureErrorCodes._

class ItemExciseProductCodePageSpec extends SpecBase with MovementSubmissionFailureFixtures {

  val page: ItemExciseProductCodePage = ItemExciseProductCodePage(testIndex2)

  def possibleErrorCodes(isForAddToList: Boolean = false): Seq[ErrorCode] = Seq(
    ItemExciseProductCodeConsignorNotApprovedToSendError(testIndex2, isForAddToList),
    ItemExciseProductCodeConsigneeNotApprovedToReceiveError(testIndex2, isForAddToList),
    ItemExciseProductCodeDestinationNotApprovedToReceiveError(testIndex2, isForAddToList),
    ItemExciseProductCodeDispatchPlaceNotAllowedError(testIndex2, isForAddToList)
  )

  "when calling isSubmissionErrorOnPage" - {

    "must return true" - {

      possibleErrorCodes().foreach { errorCode =>
        s"when the error is ${errorCode.code} and not fixed (at the specified index)" in {
          page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
            submissionFailures = Seq(itemExciseProductCodeFailure(errorCode, itemIndex = 2))
          ))) mustBe true
        }
      }

    }

    "must return false" - {

      s"when the error at the index is not in the list of possible error codes" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2).copy(errorType = "0000"))
        ))) mustBe false
      }

      possibleErrorCodes().foreach { errorCode =>
        s"when the error at the index is ${errorCode.code} but fixed" in {
          page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
            submissionFailures = Seq(itemExciseProductCodeFailure(errorCode, itemIndex = 2).copy(hasBeenFixed = true))
          ))) mustBe false
        }
      }

      "when the error exists but not at the specified index" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 1))
        ))) mustBe false
      }

      "when the error has no error location" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2).copy(errorLocation = None))
        ))) mustBe false
      }

      "no errors exist" in {
        page.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe false
      }
    }
  }

  "when calling getOriginalAttributeValueForPage" - {

    "must return Some(_)" - {

      possibleErrorCodes().foreach { errorCode =>

        s"when the error type is ${errorCode.code} and an original attribute value exists (at the specified index)" in {

          page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
            submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2))
          ))) mustBe Some("B000")
        }
      }
    }

    "must return None" - {

      s"when the error at the index is not in the list of possible error codes" in {
        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2).copy(errorType = "0000"))
        ))) mustBe None
      }

      "when the error exists but not at the specified index" in {
        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 1))
        ))) mustBe None
      }

      "when the error has no error location" in {
        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2).copy(errorLocation = None))
        ))) mustBe None
      }

      "when the original value is not defined" in {

        page.getOriginalAttributeValue(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2).copy(originalAttributeValue = None))
        ))) mustBe None
      }
    }
  }

  "when calling indexesOfMovementSubmissionErrors" - {

    "must return Seq()" - {

      possibleErrorCodes().foreach { errorCode =>

        s"for error code = ${errorCode.code}" - {

          s"when the error type does not exist in the submission failures" in {
            page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
              submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "0001", hasBeenFixed = false, originalAttributeValue = None))
            ))) mustBe Seq()
          }

          "when the error has no error location" in {
            page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
              submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2).copy(errorLocation = None))
            ))) mustBe Seq()
          }

          "when the error exists but not at the specified index" in {
            page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
              submissionFailures = Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 1))
            ))) mustBe Seq()
          }
        }
      }
    }

    "must return Seq(<index>)" - {

      s"when only one error type exists in the submission failures (at the specified index)" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 1),
            itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 2),
            movementSubmissionFailure
          )))) mustBe Seq(1)
      }
    }

    "must return Seq(<index>, <index>, ...)" - {

      //scalastyle:off
      s"when only multiple error types exists in the submission failures (at the specified index)" in {
        page.indexesOfMovementSubmissionErrors(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures =
            Seq(itemExciseProductCodeFailure(possibleErrorCodes().head, itemIndex = 1)) ++
              possibleErrorCodes().map(itemExciseProductCodeFailure(_, itemIndex = 2)) ++
              Seq(movementSubmissionFailure)
        ))) mustBe Seq(1, 2, 3, 4)
      }
    }
  }

  "when calling getSubmissionErrorCodes" - {

    "should return Some" - {

      Seq(true, false).foreach { isOnAddToList =>

        s"when isOnAddToList = $isOnAddToList" - {

          "and submission failures exist at this index" in {

            val expectedErrors = possibleErrorCodes().map(itemExciseProductCodeFailure(_, itemIndex = 2))

            page.getSubmissionErrorCodes(isOnAddToList)(dataRequest(FakeRequest(), emptyUserAnswers.copy(
              submissionFailures =
                Seq(itemDegreesPlatoFailure(1)) ++
                  expectedErrors ++
                  Seq(itemDegreesPlatoFailure(2), movementSubmissionFailure)
            ))) mustBe possibleErrorCodes(isOnAddToList)
          }
        }
      }
    }

    "should return Seq()" - {

      "when no submission failures exist" in {

        page.getSubmissionErrorCodes(isOnAddToList = false)(dataRequest(FakeRequest(), emptyUserAnswers.copy(submissionFailures = Seq.empty))) mustBe Seq.empty
      }

      "when no excise product code submission failures exist" in {

        page.getSubmissionErrorCodes(isOnAddToList = false)(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            movementSubmissionFailure
          )))) mustBe Seq.empty
      }

      "when a excise product code failure exists but at a different index" in {

        page.getSubmissionErrorCodes(isOnAddToList = false)(dataRequest(FakeRequest(), emptyUserAnswers.copy(
          submissionFailures = Seq(
            itemExciseProductCodeFailure(possibleErrorCodes().head, 1),
            movementSubmissionFailure
          )))) mustBe Seq.empty
      }
    }
  }

}
