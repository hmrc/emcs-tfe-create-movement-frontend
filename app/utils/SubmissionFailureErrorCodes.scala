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

import models.CheckMode
import models.requests.DataRequest
import play.api.mvc.Call

object SubmissionFailureErrorCodes {

  sealed trait ErrorCode {
    val code: String
    val messageKey: String
    val id: String
    def route()(implicit request: DataRequest[_]): Call
  }

  case object LocalReferenceNumberError extends ErrorCode {
    override val code = "4402"
    override val messageKey = "errors.704.lrn"
    override val id = "local-reference-number-error"
    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(request.ern, request.draftId)
  }

  case object ImportCustomsOfficeCodeError extends ErrorCode {
    override val code = "4451"
    override val messageKey = "errors.704.importCustomsOfficeCode"
    override val id = "import-customs-office-code-error"
    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ExportCustomsOfficeNumberError extends ErrorCode {
    override val code = "4425"
    override val messageKey = "errors.704.exportOffice"
    override val id = "export-customs-office-number-error"
    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ItemQuantityError extends ErrorCode {
    override val code = "4407"
    override val messageKey = "errors.704.itemQuantity"
    override val id = "item-quantity-error"
    override def route()(implicit request: DataRequest[_]): Call =
      ???
  }

  case object InvalidOrMissingConsigneeError extends ErrorCode {
    override val code = "4405"
    override val messageKey = "errors.704.invalidOrMissingConsignee"
    override val id = "invalid-or-missing-consignee-error"
    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object LinkIsPendingError extends ErrorCode {
    override val code = "4413"
    override val messageKey = "errors.704.linkIsPending"
    override val id = "link-is-pending-error"
    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object LinkIsAlreadyUsedError extends ErrorCode {
    override val code = "4414"
    override val messageKey = "errors.704.linkIsAlreadyUsed"
    override val id = "link-is-already-used-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object LinkIsWithdrawnError extends ErrorCode {
    override val code = "4415"
    override val messageKey = "errors.704.linkIsWithdrawn"
    override val id = "link-is-withdrawn-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object LinkIsCancelledError extends ErrorCode {
    override val code = "4416"
    override val messageKey = "errors.704.linkIsCancelled"
    override val id = "link-is-cancelled-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object LinkIsExpiredError extends ErrorCode {
    override val code = "4417"
    override val messageKey = "errors.704.linkIsExpired"
    override val id = "link-is-expired-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object LinkMissingOrInvalidError extends ErrorCode {
    override val code = "4418"
    override val messageKey = "errors.704.linkMissingOrInvalid"
    override val id = "link-missing-or-invalid-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object DirectDeliveryNotAllowedError extends ErrorCode {
    override val code = "4419"
    override val messageKey = "errors.704.directDeliveryNotAllowed"
    override val id = "direct-delivery-not-allowed-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ConsignorNotAuthorisedError extends ErrorCode {
    override val code = "4420"
    override val messageKey = "errors.704.consignorNotAuthorised"
    override val id = "consignor-not-authorised-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object RegisteredConsignorToRegisteredConsigneeError extends ErrorCode {
    override val code = "4423"
    override val messageKey = "errors.704.registeredConsignorToRegisteredConsignee"
    override val id = "registered-consignor-to-registered-consignee-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ConsigneeRoleInvalidError extends ErrorCode {
    override val code = "4455"
    override val messageKey = "errors.704.consigneeRoleInvalid"
    override val id = "consignee-role-invalid-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ExciseIdForTaxWarehouseOfDestinationInvalidError extends ErrorCode {
    override val code = "4406"
    override val messageKey = "errors.704.exciseIdForTaxWarehouseOfDestinationInvalid"
    override val id = "excise-id-for-tax-warehouse-of-destination-invalid-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError extends ErrorCode {
    override val code = "4421"
    override val messageKey = "errors.704.exciseIdForTaxWarehouseOfDestinationNeedsConsignee"
    override val id = "excise-id-for-tax-warehouse-of-destination-needs-consignee-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  case object ExciseIdForTaxWarehouseInvalid extends ErrorCode {
    override val code = "4456"
    override val messageKey = "errors.704.exciseIdForTaxWarehouseInvalid"
    override val id = "excise-id-for-tax-warehouse-invalid-error"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  }

  object ErrorCode {

    //scalastyle:off cyclomatic.complexity
    def apply(errorType: String): ErrorCode = errorType match {
      case "4402" => LocalReferenceNumberError
      case "4451" => ImportCustomsOfficeCodeError
      case "4425" => ExportCustomsOfficeNumberError
      case "4407" => ItemQuantityError
      case "4405" => InvalidOrMissingConsigneeError
      case "4413" => LinkIsPendingError
      case "4414" => LinkIsAlreadyUsedError
      case "4415" => LinkIsWithdrawnError
      case "4416" => LinkIsCancelledError
      case "4417" => LinkIsExpiredError
      case "4418" => LinkMissingOrInvalidError
      case "4419" => DirectDeliveryNotAllowedError
      case "4420" => ConsignorNotAuthorisedError
      case "4423" => RegisteredConsignorToRegisteredConsigneeError
      case "4455" => ConsigneeRoleInvalidError
      case "4406" => ExciseIdForTaxWarehouseOfDestinationInvalidError
      case "4421" => ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError
      case "4456" => ExciseIdForTaxWarehouseInvalid
    }
  }
}

