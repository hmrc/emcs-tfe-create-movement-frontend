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

import models.requests.DataRequest
import models.{CheckMode, Index}
import play.api.mvc.Call

sealed trait SubmissionError {
  val code: String
  val messageKey: String
  val id: String
  def route()(implicit request: DataRequest[_]): Call

  val index: Option[Index] = None
}

case object LocalReferenceNumberError extends SubmissionError {
  override val code = "4402"
  override val messageKey = "errors.704.lrn"
  override val id = "local-reference-number-error"
  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(request.ern, request.draftId)
}

case object ImportCustomsOfficeCodeError extends SubmissionError {
  override val code = "4451"
  override val messageKey = "errors.704.importCustomsOfficeCode"
  override val id = "import-customs-office-code-error"
  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object ExportCustomsOfficeNumberError extends SubmissionError {
  override val code = "4425"
  override val messageKey = "errors.704.exportOffice"
  override val id = "export-customs-office-number-error"
  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object InvalidOrMissingConsigneeError extends SubmissionError {
  override val code = "4405"
  override val messageKey = "errors.704.invalidOrMissingConsignee"
  override val id = "invalid-or-missing-consignee-error"
  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object LinkIsPendingError extends SubmissionError {
  override val code = "4413"
  override val messageKey = "errors.704.linkIsPending"
  override val id = "link-is-pending-error"
  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object LinkIsAlreadyUsedError extends SubmissionError {
  override val code = "4414"
  override val messageKey = "errors.704.linkIsAlreadyUsed"
  override val id = "link-is-already-used-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object LinkIsWithdrawnError extends SubmissionError {
  override val code = "4415"
  override val messageKey = "errors.704.linkIsWithdrawn"
  override val id = "link-is-withdrawn-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object LinkIsCancelledError extends SubmissionError {
  override val code = "4416"
  override val messageKey = "errors.704.linkIsCancelled"
  override val id = "link-is-cancelled-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object LinkIsExpiredError extends SubmissionError {
  override val code = "4417"
  override val messageKey = "errors.704.linkIsExpired"
  override val id = "link-is-expired-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object LinkMissingOrInvalidError extends SubmissionError {
  override val code = "4418"
  override val messageKey = "errors.704.linkMissingOrInvalid"
  override val id = "link-missing-or-invalid-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object DirectDeliveryNotAllowedError extends SubmissionError {
  override val code = "4419"
  override val messageKey = "errors.704.directDeliveryNotAllowed"
  override val id = "direct-delivery-not-allowed-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object ConsignorNotAuthorisedError extends SubmissionError {
  override val code = "4420"
  override val messageKey = "errors.704.consignorNotAuthorised"
  override val id = "consignor-not-authorised-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object RegisteredConsignorToRegisteredConsigneeError extends SubmissionError {
  override val code = "4423"
  override val messageKey = "errors.704.registeredConsignorToRegisteredConsignee"
  override val id = "registered-consignor-to-registered-consignee-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object ConsigneeRoleInvalidError extends SubmissionError {
  override val code = "4455"
  override val messageKey = "errors.704.consigneeRoleInvalid"
  override val id = "consignee-role-invalid-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case class ItemQuantityError(idx: Index, isForAddToList: Boolean) extends SubmissionError {
  override val code = ItemQuantityError.code
  override val messageKey = s"errors.704.items.quantity${if (isForAddToList) ".addToList" else ""}"
  override val id = s"fix-item-${idx.displayIndex}-quantity"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.items.routes.ItemQuantityController.onPageLoad(request.ern, request.draftId, idx, CheckMode)

  override val index = Some(idx)
}
object ItemQuantityError {
  val code = "4407"
}

case class ItemDegreesPlatoError(idx: Index, isForAddToList: Boolean) extends SubmissionError {
  override val code = ItemDegreesPlatoError.code
  override val messageKey = s"errors.704.items.degreesPlato${if (isForAddToList) ".addToList" else ""}"
  override val id = s"fix-item-${idx.displayIndex}-degrees-plato"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode)

  override val index = Some(idx)
}

object ItemDegreesPlatoError {
  val code = "4445"
}

case object ExciseIdForTaxWarehouseOfDestinationInvalidError extends SubmissionError {
  override val code = "4406"
  override val messageKey = "errors.704.exciseIdForTaxWarehouseOfDestinationInvalid"
  override val id = "excise-id-for-tax-warehouse-of-destination-invalid-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError extends SubmissionError {
  override val code = "4421"
  override val messageKey = "errors.704.exciseIdForTaxWarehouseOfDestinationNeedsConsignee"
  override val id = "excise-id-for-tax-warehouse-of-destination-needs-consignee-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

case object ExciseIdForTaxWarehouseInvalid extends SubmissionError {
  override val code = "4456"
  override val messageKey = "errors.704.exciseIdForTaxWarehouseInvalid"
  override val id = "excise-id-for-tax-warehouse-invalid-error"

  override def route()(implicit request: DataRequest[_]): Call =
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
}

object SubmissionError {

  //scalastyle:off cyclomatic.complexity
  def apply(errorType: String): SubmissionError = errorType match {
    case LocalReferenceNumberError.code => LocalReferenceNumberError
    case ImportCustomsOfficeCodeError.code => ImportCustomsOfficeCodeError
    case ExportCustomsOfficeNumberError.code => ExportCustomsOfficeNumberError
    case InvalidOrMissingConsigneeError.code => InvalidOrMissingConsigneeError
    case LinkIsPendingError.code => LinkIsPendingError
    case LinkIsAlreadyUsedError.code => LinkIsAlreadyUsedError
    case LinkIsWithdrawnError.code => LinkIsWithdrawnError
    case LinkIsCancelledError.code => LinkIsCancelledError
    case LinkIsExpiredError.code => LinkIsExpiredError
    case LinkMissingOrInvalidError.code => LinkMissingOrInvalidError
    case DirectDeliveryNotAllowedError.code => DirectDeliveryNotAllowedError
    case ConsignorNotAuthorisedError.code => ConsignorNotAuthorisedError
    case RegisteredConsignorToRegisteredConsigneeError.code => RegisteredConsignorToRegisteredConsigneeError
    case ConsigneeRoleInvalidError.code => ConsigneeRoleInvalidError
    case ExciseIdForTaxWarehouseOfDestinationInvalidError.code => ExciseIdForTaxWarehouseOfDestinationInvalidError
    case ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError.code => ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError
    case ExciseIdForTaxWarehouseInvalid.code => ExciseIdForTaxWarehouseInvalid
    case errorCode => throw new IllegalArgumentException(s"Invalid submission error code: $errorCode")
  }

  //scalastyle:off cyclomatic.complexity
  def apply(errorType: String, idx: Index, isForAddToList: Boolean = false): SubmissionError = errorType match {
    case ItemQuantityError.code => ItemQuantityError(idx, isForAddToList)
    case ItemDegreesPlatoError.code => ItemDegreesPlatoError(idx, isForAddToList)
    case errorCode => throw new IllegalArgumentException(s"Invalid submission error code: $errorCode")
  }
}