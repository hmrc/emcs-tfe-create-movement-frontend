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

package fixtures

import models.MovementSubmissionFailure
import models.validation.UIErrorModel
import utils._

import java.time.LocalDate

trait MovementSubmissionFailureFixtures extends BaseFixtures {

  val movementSubmissionFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = LocalReferenceNumberError.code,
    errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
    errorLocation = None,
    originalAttributeValue = Some(testLrn),
    hasBeenFixed = false
  )

  val importCustomsOfficeCodeFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ImportCustomsOfficeCodeError.code,
    errorReason = "The customs office reference number you have entered is not valid. Please amend your entry and resubmit",
    errorLocation = None,
    originalAttributeValue = Some(testGBImportCustomsOffice),
    hasBeenFixed = false
  )

  val consigneeExciseFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = InvalidOrMissingConsigneeError.code,
    errorReason = "Invalid or missing Consignee on SEED",
    errorLocation = None,
    originalAttributeValue = Some(testErn),
    hasBeenFixed = false
  )

  val destinationWarehouseExciseFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = PlaceOfDestinationExciseIdInvalidError.code,
    errorReason = "Invalid or missing Place of Delivery on SEED",
    errorLocation = None,
    originalAttributeValue = Some(testErn),
    hasBeenFixed = false
  )

  val consignorNotApprovedToSendFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ConsignorNotApprovedToSendError.code,
    errorReason = "The link between the nature of goods on the draft movement and those allowed for the consignor as held on SEED is missing or invalid",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = false
  )

  val consigneeNotApprovedToRetrieveFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ConsigneeNotApprovedToReceiveError.code,
    errorReason = "The link between the nature of goods on the draft movement and those allowed for the consignee as held on SEED is missing or invalid",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = false
  )

  val dispatchPlaceNotAllowedFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = DispatchPlaceNotAllowedError.code,
    errorReason = "Dispatch place not approved to send goods",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = false
  )

  val destinationNotApprovedToReceiveFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = DestinationNotApprovedToReceiveError.code,
    errorReason = "Destination not approved to receive goods",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = false
  )

  val itemQuantityFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ItemQuantityError.code,
    errorReason = "The quantitiy entered exceeds the amount approved for this Temporary Consignment Authorisation (TCA). Please check and amend your entry.",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = false
  )

  val itemDegreesPlatoFailure: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ItemDegreesPlatoError.code,
    errorReason = "The alcoholic strength for wine and spirits you have entered is not valid.  Please amend your entry and resubmit",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = false
  )

  val dispatchWarehouseInvalidOrMissingOnSeedError: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = DispatchWarehouseInvalidOrMissingOnSeedError.code,
    errorReason = "The Excise Warehouse Registration Number you have entered is not recognised by SEED. Please amend your entry.",
    errorLocation = None,
    originalAttributeValue = Some(testErn),
    hasBeenFixed = false
  )

  def dispatchDateInPastValidationError(msg: String = "Error",
                                        value: LocalDate = LocalDate.of(2022,1,1)): MovementSubmissionFailure =
    UIErrorModel(DispatchDateInPastValidationError, msg, value).asSubmissionFailure

  def dispatchDateInFutureValidationError(msg: String = "Error",
                                        value: LocalDate = LocalDate.of(2026, 1, 1)): MovementSubmissionFailure =
    UIErrorModel(DispatchDateInFutureValidationError, msg, value).asSubmissionFailure

}
