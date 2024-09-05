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
import controllers.sections.consignee.routes._
import controllers.sections.destination.routes._
import controllers.sections.exportInformation.routes._
import controllers.sections.importInformation.routes._
import controllers.sections.info.routes._
import controllers.sections.items.routes._
import fixtures.messages.ValidationErrorMessages
import models.CheckMode
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class SubmissionFailureErrorCodesSpec extends SpecBase {

  "ErrorCode" - {

    "for indexed errors" - {
      Seq(true, false).foreach { isForAddToList =>

        s"when isForAddToList = $isForAddToList" - {

          "should return ItemQuantityError for error code: 4407" - {
            SubmissionError.apply("4407", testIndex1, isForAddToList) mustBe ItemQuantityError(testIndex1, isForAddToList)
          }

          "should return ItemDegreesPlatoError for error code: 4445" - {

            SubmissionError.apply("4445", testIndex1, isForAddToList) mustBe ItemDegreesPlatoError(testIndex1, isForAddToList)
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
        PlaceOfDestinationExciseIdForTaxWarehouseInvalidError -> "4456",
        DispatchWarehouseInvalidOrMissingOnSeedError -> "4404",
        DispatchWarehouseInvalidError -> "4458",
        DispatchWarehouseConsignorDoesNotManageWarehouseError -> "4461",
        DispatchDateInFutureValidationError -> "8085",
        DispatchDateInPastValidationError -> "8084",
        ConsignorNotApprovedToSendError -> "4408",
        ConsigneeNotApprovedToReceiveError -> "4409",
        DestinationNotApprovedToReceiveError -> "4410",
        DispatchPlaceNotAllowedError -> "4527"
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

      Seq[(SubmissionError, Option[String])](
        LocalReferenceNumberError -> Some(LocalReferenceNumberController.onPageLoad(testErn, testDraftId, CheckMode).url),
        ImportCustomsOfficeCodeError -> Some(ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, CheckMode).url),
        ExportCustomsOfficeNumberError -> Some(ExportCustomsOfficeController.onPageLoad(testErn, testDraftId, CheckMode).url),
        InvalidOrMissingConsigneeError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        LinkIsPendingError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        LinkIsAlreadyUsedError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        LinkIsWithdrawnError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        LinkIsCancelledError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        LinkIsExpiredError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        LinkMissingOrInvalidError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        DirectDeliveryNotAllowedError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        ConsignorNotAuthorisedError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        RegisteredConsignorToRegisteredConsigneeError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        ConsigneeRoleInvalidError -> Some(ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        PlaceOfDestinationExciseIdInvalidError -> Some(DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError -> Some(DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        PlaceOfDestinationExciseIdForTaxWarehouseInvalidError -> Some(DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url),
        DispatchDateInFutureValidationError -> Some(DispatchDetailsController.onPageLoad(testErn, testDraftId, CheckMode).url),
        DispatchDateInPastValidationError -> Some(DispatchDetailsController.onPageLoad(testErn, testDraftId, CheckMode).url),
        ItemQuantityError(itemIndex, isForAddToList) -> Some(ItemQuantityController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url),
        ItemDegreesPlatoError(itemIndex, isForAddToList) -> Some(ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, itemIndex, CheckMode).url),
        ConsignorNotApprovedToSendError -> None,
        ConsigneeNotApprovedToReceiveError -> None,
        DestinationNotApprovedToReceiveError -> None,
        DispatchPlaceNotAllowedError -> None
      ).foreach {
        case (error, expectedUrl) =>
          error.route().map(_.url) mustBe expectedUrl
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

  "Error Message content" - {

    Seq(ValidationErrorMessages.English).foreach { messagesForLang =>

      implicit val msgs = messages(candidates = Seq(messagesForLang.lang))

      s"when output in language code of '${messagesForLang.lang.code}'" - {

        s"must render correct content for $DispatchDateInFutureValidationError" in {
          msgs(DispatchDateInFutureValidationError.messageKey) mustBe messagesForLang.dispatchDateInFutureValidationError
        }

        s"must render correct content for $DispatchDateInPastValidationError" in {
          msgs(DispatchDateInPastValidationError.messageKey) mustBe messagesForLang.dispatchDateInPastValidationError(appConfig.maxDispatchDateFutureDays)
        }
      }
    }
  }
}
