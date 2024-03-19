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

package utils

import base.SpecBase

import scala.collection.Seq

class SubmissionFailureErrorCodesSpec extends SpecBase {

  "ErrorCode.apply" - {

    "for indexed errors" - {
      Seq(true, false).foreach { isForAddToList =>

        s"when isForAddToList = $isForAddToList" - {

          "should return ItemQuantityError for error code: 4407" - {
            SubmissionError.apply("4407", testIndex1, isForAddToList) mustBe ItemQuantityError(testIndex1, isForAddToList)
          }

          "should return ItemDegreesPlatoError for error code: 4445" - {

            SubmissionError.apply("4445", testIndex1, isForAddToList) mustBe ItemDegreesPlatoError(testIndex1, isForAddToList)
          }

          "should return ItemExciseProductCodeConsignorNotApprovedToSendError for error code: 4408" - {

            SubmissionError.apply("4408", testIndex1, isForAddToList) mustBe ItemExciseProductCodeConsignorNotApprovedToSendError(testIndex1, isForAddToList)
          }

          "should return ItemExciseProductCodeConsigneeNotApprovedToReceiveError for error code: 4409" - {

            SubmissionError.apply("4409", testIndex1, isForAddToList) mustBe ItemExciseProductCodeConsigneeNotApprovedToReceiveError(testIndex1, isForAddToList)
          }

          "should return ItemExciseProductCodeDestinationNotApprovedToReceiveError for error code: 4410" - {

            SubmissionError.apply("4410", testIndex1, isForAddToList) mustBe ItemExciseProductCodeDestinationNotApprovedToReceiveError(testIndex1, isForAddToList)
          }

          "should return ItemExciseProductCodeDispatchPlaceNotAllowedError for error code: 4527" - {

            SubmissionError.apply("4527", testIndex1, isForAddToList) mustBe ItemExciseProductCodeDispatchPlaceNotAllowedError(testIndex1, isForAddToList)
          }
        }
      }
    }

    "must return the correct SubmissionError (non-indexed)" - {

      Seq(
        LocalReferenceNumberError,
        ImportCustomsOfficeCodeError,
        ExportCustomsOfficeNumberError,
        InvalidOrMissingConsigneeError,
        LinkIsPendingError,
        LinkIsAlreadyUsedError,
        LinkIsWithdrawnError,
        LinkIsCancelledError,
        LinkIsExpiredError,
        LinkMissingOrInvalidError,
        DirectDeliveryNotAllowedError,
        ConsignorNotAuthorisedError,
        RegisteredConsignorToRegisteredConsigneeError,
        ConsigneeRoleInvalidError
      ).foreach { submissionError =>

        s"when given error code ${submissionError.code}" in {

          val expectedResult = submissionError
          val actualResult = SubmissionError(submissionError.code)

          actualResult mustBe expectedResult
        }
      }
    }

    "when given an invalid error code (non-indexed)" in {

      val actualResult = intercept[IllegalArgumentException] {
        SubmissionError("invalid code")
      }.getMessage

      val expectedResult = "Invalid submission error code: invalid code"

      actualResult mustBe expectedResult
    }

    "when given an invalid error code (indexed)" in {

      val actualResult = intercept[IllegalArgumentException] {
        SubmissionError("invalid code", testIndex1)
      }.getMessage

      val expectedResult = "Invalid submission error code: invalid code"

      actualResult mustBe expectedResult
    }
  }
}
