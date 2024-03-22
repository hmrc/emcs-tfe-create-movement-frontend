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
import utils._

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
    originalAttributeValue = Some(testImportCustomsOffice),
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

  def itemQuantityFailure(itemIndex: Int): MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ItemQuantityError.code,
    errorReason = "The quantitiy entered exceeds the amount approved for this Temporary Consignment Authorisation (TCA). Please check and amend your entry.",
    errorLocation = Some(s"/IE815[1]/Body[1]/SubmittedDraftOfEADESAD[1]/BodyEadEsad[$itemIndex]/Quantity[1]"),
    originalAttributeValue = Some("10000"),
    hasBeenFixed = false
  )


  def itemDegreesPlatoFailure(itemIndex: Int): MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = ItemDegreesPlatoError.code,
    errorReason = "The alcoholic strength for wine and spirits you have entered is not valid.  Please amend your entry and resubmit",
    errorLocation = Some(s"/IE815[1]/Body[1]/SubmittedDraftOfEADESAD[1]/BodyEadEsad[$itemIndex]/DegreePlato[1]"),
    originalAttributeValue = Some("10"),
    hasBeenFixed = false
  )

  def itemExciseProductCodeFailure(errorCode: SubmissionError, itemIndex: Int): MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = errorCode.code,
    errorReason = "Not used",
    errorLocation = Some(s"/IE815[1]/Body[1]/SubmittedDraftOfEADESAD[1]/BodyEadEsad[$itemIndex]/ExciseProductCode[1]"),
    originalAttributeValue = Some("B000"),
    hasBeenFixed = false
  )

  val dispatchWarehouseInvalidOrMissingOnSeedError: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = DispatchWarehouseInvalidOrMissingOnSeedError.code,
    errorReason = "The Excise Warehouse Registration Number you have entered is not recognised by SEED. Please amend your entry.",
    errorLocation = None,
    originalAttributeValue = Some(testErn),
    hasBeenFixed = false
  )

}
