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
import models.sections.info.DispatchDetailsModel
import models.{CheckMode, Index}
import pages.QuestionPage
import pages.sections.info.DispatchDetailsPage
import play.api.mvc.Call

sealed trait SubmissionError {
  val code: String
  val messageKey: String
  val id: String
  def nonFixable()(implicit request: DataRequest[_]): Boolean = route.isEmpty

  def route()(implicit request: DataRequest[_]): Option[Call] = None

  val index: Option[Index] = None
}

sealed trait UIError[A] extends SubmissionError {
  val page: QuestionPage[A]
}

case object LocalReferenceNumberError extends SubmissionError {
  override val code = "4402"
  override val messageKey = "errors.704.lrn"
  override val id = "local-reference-number-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object ImportCustomsOfficeCodeError extends SubmissionError {
  override val code = "4451"
  override val messageKey = "errors.704.importCustomsOfficeCode"
  override val id = "import-customs-office-code-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object ExportCustomsOfficeNumberError extends SubmissionError {
  override val code = "4425"
  override val messageKey = "errors.704.exportOffice"
  override val id = "export-customs-office-number-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object InvalidOrMissingConsigneeError extends SubmissionError {
  override val code = "4405"
  override val messageKey = "errors.704.invalidOrMissingConsignee"
  override val id = "invalid-or-missing-consignee-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object LinkIsPendingError extends SubmissionError {
  override val code = "4413"
  override val messageKey = "errors.704.linkIsPending"
  override val id = "link-is-pending-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object LinkIsAlreadyUsedError extends SubmissionError {
  override val code = "4414"
  override val messageKey = "errors.704.linkIsAlreadyUsed"
  override val id = "link-is-already-used-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object LinkIsWithdrawnError extends SubmissionError {
  override val code = "4415"
  override val messageKey = "errors.704.linkIsWithdrawn"
  override val id = "link-is-withdrawn-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object LinkIsCancelledError extends SubmissionError {
  override val code = "4416"
  override val messageKey = "errors.704.linkIsCancelled"
  override val id = "link-is-cancelled-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object LinkIsExpiredError extends SubmissionError {
  override val code = "4417"
  override val messageKey = "errors.704.linkIsExpired"
  override val id = "link-is-expired-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object LinkMissingOrInvalidError extends SubmissionError {
  override val code = "4418"
  override val messageKey = "errors.704.linkMissingOrInvalid"
  override val id = "link-missing-or-invalid-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object DirectDeliveryNotAllowedError extends SubmissionError {
  override val code = "4419"
  override val messageKey = "errors.704.directDeliveryNotAllowed"
  override val id = "direct-delivery-not-allowed-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object ConsignorNotAuthorisedError extends SubmissionError {
  override val code = "4420"
  override val messageKey = "errors.704.consignorNotAuthorised"
  override val id = "consignor-not-authorised-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object RegisteredConsignorToRegisteredConsigneeError extends SubmissionError {
  override val code = "4423"
  override val messageKey = "errors.704.registeredConsignorToRegisteredConsignee"
  override val id = "registered-consignor-to-registered-consignee-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object ConsigneeRoleInvalidError extends SubmissionError {
  override val code = "4455"
  override val messageKey = "errors.704.consigneeRoleInvalid"
  override val id = "consignee-role-invalid-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object DispatchWarehouseInvalidOrMissingOnSeedError extends SubmissionError {
  override val code = "4404"
  override val messageKey = "errors.704.dispatchWarehouseInvalidOrMissingOnSeedError"
  override val id = "dispatch-warehouse-invalid-or-missing-on-seed-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object DispatchWarehouseInvalidError extends SubmissionError {
  override val code = "4458"
  override val messageKey = "errors.704.dispatchWarehouseInvalidError"
  override val id = "dispatch-warehouse-invalid-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object DispatchWarehouseConsignorDoesNotManageWarehouseError extends SubmissionError {
  override val code = "4461"
  override val messageKey = "errors.704.dispatchWarehouseConsignorDoesNotManageWarehouseError"
  override val id = "dispatch-warehouse-consignor-does-not-manage-warehouse-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case class ItemQuantityError(idx: Index, isForAddToList: Boolean) extends SubmissionError {
  override val code = ItemQuantityError.code
  override val messageKey = s"errors.704.items.quantity${if (isForAddToList) ".addToList" else ""}"
  override val id = s"fix-item-${idx.displayIndex}-quantity"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.items.routes.ItemQuantityController.onPageLoad(request.ern, request.draftId, idx, CheckMode)
  )

  override val index = Some(idx)
}

object ItemQuantityError {
  val code = "4407"
}

case class ItemDegreesPlatoError(idx: Index, isForAddToList: Boolean) extends SubmissionError {
  override val code = ItemDegreesPlatoError.code
  override val messageKey = s"errors.704.items.degreesPlato${if (isForAddToList) ".addToList" else ""}"
  override val id = s"fix-item-${idx.displayIndex}-degrees-plato"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode)
  )

  override val index = Some(idx)
}

object ItemDegreesPlatoError {
  val code = "4445"
}

object ConsignorNotApprovedToSendError extends SubmissionError {
  override val code = "4408"
  override val messageKey = "errors.704.consignorNotApprovedToSendError"
  override val id = s"fix-consignor-not-approved-to-send"
}

object ConsigneeNotApprovedToReceiveError extends SubmissionError {
  override val code = "4409"
  override val messageKey = s"errors.704.consigneeNotApprovedToReceiveError"
  override val id = s"fix-consignee-not-approved-to-receive"
}

object DestinationNotApprovedToReceiveError extends SubmissionError {
  override val code = "4410"
  override val messageKey = "errors.704.destinationNotApprovedToReceiveError"
  override val id = s"fix-destination-not-approved-to-receive"
}

object DispatchPlaceNotAllowedError extends SubmissionError {
  override val code = "4527"
  override val messageKey = "errors.704.dispatchPlaceNotAllowed"
  override val id = s"fix-dispatch-place-not-allowed"
}

case object PlaceOfDestinationExciseIdInvalidError extends SubmissionError {
  override val code = "4406"
  override val messageKey = "errors.704.placeOfDestinationExciseIdInvalidError"
  override val id = "excise-id-for-tax-warehouse-of-destination-invalid-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError extends SubmissionError {
  override val code = "4412"
  override val messageKey = "errors.704.placeOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError"
  override val id = "excise-id-for-tax-warehouse-of-destination-needs-consignee-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object PlaceOfDestinationExciseIdForTaxWarehouseInvalidError extends SubmissionError {
  override val code = "4456"
  override val messageKey = "errors.704.placeOfDestinationExciseIdForTaxWarehouseInvalidError"
  override val id = "excise-id-for-tax-warehouse-invalid-error"

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object DispatchDateInPastValidationError extends UIError[DispatchDetailsModel] {
  override val code = "8084"
  override val messageKey = "errors.ui.dispatchDate.inPast"
  override val id = "dispatch-date-validation-error"
  override val page = DispatchDetailsPage(isOnPreDraftFlow = false)

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.info.routes.DispatchDetailsController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
}

case object DispatchDateInFutureValidationError extends UIError[DispatchDetailsModel] {
  override val code = "8085"
  override val messageKey = "errors.ui.dispatchDate.inFuture"
  override val id = "dispatch-date-validation-error"
  override val page = DispatchDetailsPage(isOnPreDraftFlow = false)

  override def route()(implicit request: DataRequest[_]): Option[Call] = Some(
    controllers.sections.info.routes.DispatchDetailsController.onPageLoad(request.ern, request.draftId, CheckMode)
  )
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
    case PlaceOfDestinationExciseIdInvalidError.code => PlaceOfDestinationExciseIdInvalidError
    case PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError.code => PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError
    case PlaceOfDestinationExciseIdForTaxWarehouseInvalidError.code => PlaceOfDestinationExciseIdForTaxWarehouseInvalidError
    case DispatchWarehouseInvalidOrMissingOnSeedError.code => DispatchWarehouseInvalidOrMissingOnSeedError
    case DispatchWarehouseInvalidError.code => DispatchWarehouseInvalidError
    case DispatchWarehouseConsignorDoesNotManageWarehouseError.code => DispatchWarehouseConsignorDoesNotManageWarehouseError
    case DispatchDateInPastValidationError.code => DispatchDateInPastValidationError
    case DispatchDateInFutureValidationError.code => DispatchDateInFutureValidationError
    case ConsignorNotApprovedToSendError.code => ConsignorNotApprovedToSendError
    case ConsigneeNotApprovedToReceiveError.code => ConsigneeNotApprovedToReceiveError
    case DestinationNotApprovedToReceiveError.code => DestinationNotApprovedToReceiveError
    case DispatchPlaceNotAllowedError.code => DispatchPlaceNotAllowedError
    case errorCode => throw new IllegalArgumentException(s"Invalid submission error code: $errorCode")
  }

  //scalastyle:off cyclomatic.complexity
  def apply(errorType: String, idx: Index, isForAddToList: Boolean = false): SubmissionError = errorType match {
    case ItemQuantityError.code => ItemQuantityError(idx, isForAddToList)
    case ItemDegreesPlatoError.code => ItemDegreesPlatoError(idx, isForAddToList)
    case errorCode => throw new IllegalArgumentException(s"Invalid submission error code: $errorCode")
  }
}