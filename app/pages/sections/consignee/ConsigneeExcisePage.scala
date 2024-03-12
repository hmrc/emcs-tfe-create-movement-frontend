/*
 * Copyright 2023 HM Revenue & Customs
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

package pages.sections.consignee

import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.SubmissionFailureErrorCodes._

case object ConsigneeExcisePage extends QuestionPage[String] {

  override val toString: String = "exciseRegistrationNumber"
  override val path: JsPath = ConsigneeSection.path \ toString

  val possibleErrors = Seq(
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
  )

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean = {
    request.userAnswers.submissionFailures.exists(error =>
      possibleErrors.map(_.code).contains(error.errorType) && !error.hasBeenFixed
    )
  }

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    request.userAnswers.submissionFailures.find(error =>
      possibleErrors.map(_.code).contains(error.errorType)
    ).flatMap(_.originalAttributeValue)

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    request.userAnswers.submissionFailures.zipWithIndex.collect {
      case (error, index) if possibleErrors.map(_.code).contains(error.errorType) => index
    }

  override def getMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[ErrorCode] = {
    request.userAnswers.submissionFailures.collect {
      case error if possibleErrors.map(_.code).contains(error.errorType) && !error.hasBeenFixed => ErrorCode(error.errorType)
    }
  }
}
