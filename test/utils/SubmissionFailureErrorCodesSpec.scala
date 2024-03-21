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
import controllers.sections.consignee.routes._
import controllers.sections.items.routes._
import controllers.sections.importInformation.routes._
import controllers.sections.exportInformation.routes._
import controllers.sections.destination.routes._
import controllers.sections.info.routes._

import models.CheckMode
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

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

    "must return the correct SubmissionError and code (non-indexed)" - {

      Seq(
        LocalReferenceNumberError -> "4402",
        ImportCustomsOfficeCodeError -> "4451",
        ExportCustomsOfficeNumberError -> "4425",
        InvalidOrMissingConsigneeError -> "4405",
        LinkIsPendingError -> "4413",
        LinkIsAlreadyUsedError -> "4414",
        LinkIsWithdrawnError -> "4415",
        LinkIsCancelledError -> "4416",
        LinkIsExpiredError -> "4417",
        LinkMissingOrInvalidError -> "4418",
        DirectDeliveryNotAllowedError -> "4419",
        ConsignorNotAuthorisedError -> "4420",
        RegisteredConsignorToRegisteredConsigneeError -> "4423",
        ConsigneeRoleInvalidError -> "4455",
        PlaceOfDestinationExciseIdInvalidError -> "4406",
        PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError -> "4412",
        PlaceOfDestinationExciseIdForTaxWarehouseInvalidError -> "4456"
      ).foreach {
        case (submissionError, expectedErrorCode) =>

          s"when given error code ${submissionError.code}" in {

            val expectedResult = submissionError
            val actualResult = SubmissionError(submissionError.code)

            actualResult mustBe expectedResult
            actualResult.code mustBe expectedErrorCode
          }
      }
    }

    "must have the expected route" in {

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

      val itemIndex = 1
      val isForAddToList = true

      Seq(
        LocalReferenceNumberError -> LocalReferenceNumberController.onPageLoad(testErn, testDraftId).url,
        ImportCustomsOfficeCodeError -> ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, CheckMode).url,
        ExportCustomsOfficeNumberError -> ExportCustomsOfficeController.onPageLoad(testErn, testDraftId, CheckMode).url,
        InvalidOrMissingConsigneeError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        LinkIsPendingError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        LinkIsAlreadyUsedError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        LinkIsWithdrawnError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        LinkIsCancelledError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        LinkIsExpiredError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        LinkMissingOrInvalidError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        DirectDeliveryNotAllowedError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        ConsignorNotAuthorisedError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        RegisteredConsignorToRegisteredConsigneeError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        ConsigneeRoleInvalidError -> ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        ItemQuantityError(itemIndex, isForAddToList) -> ItemQuantityController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url,
        ItemDegreesPlatoError(itemIndex, isForAddToList) -> ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url,
        ItemExciseProductCodeConsignorNotApprovedToSendError(itemIndex, isForAddToList) ->
          ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url,
        ItemExciseProductCodeConsigneeNotApprovedToReceiveError(itemIndex, isForAddToList) ->
          ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url,
        ItemExciseProductCodeDestinationNotApprovedToReceiveError(itemIndex, isForAddToList) ->
          ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url,
        ItemExciseProductCodeDispatchPlaceNotAllowedError(itemIndex, isForAddToList) ->
          ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url,
        PlaceOfDestinationExciseIdInvalidError -> DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError -> DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
        PlaceOfDestinationExciseIdForTaxWarehouseInvalidError -> DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url
      ).foreach {
        case (error, expectedUrl) =>
          error.route().url mustBe expectedUrl
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
