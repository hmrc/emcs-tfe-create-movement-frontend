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

package pages.sections.dispatch

import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.SubmissionFailureErrorCodes.{dispatchWarehouseExciseIDConsignorLinkError, dispatchWarehouseExciseIDInvalid, dispatchWarehouseExciseIDInvalid2}

case object DispatchWarehouseExcisePage extends QuestionPage[String] {
  override val toString: String = "dispatchWarehouseExcise"
  override val path: JsPath = DispatchSection.path \ toString


  def get704ErrorCodeMessage(implicit request: DataRequest[_]): String =
    if (request.userAnswers.submissionFailures.exists(error => error.errorType == dispatchWarehouseExciseIDInvalid || error.errorType == dispatchWarehouseExciseIDInvalid2)) "errors.704.dispatchWareHouseExciseID.input"
    else "errors.704.dispatchWareHouseConsignorLinkExciseID.input"
  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.submissionFailures.exists(error => (error.errorType == dispatchWarehouseExciseIDInvalid || error.errorType == dispatchWarehouseExciseIDInvalid2 || error.errorType == dispatchWarehouseExciseIDConsignorLinkError) && !error.hasBeenFixed)

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] = {
    request.userAnswers.submissionFailures.find(_.errorType == dispatchWarehouseExciseIDInvalid).flatMap(_.originalAttributeValue)
  }

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    Seq(request.userAnswers.submissionFailures.indexWhere(_.errorType == dispatchWarehouseExciseIDInvalid), request.userAnswers.submissionFailures.indexWhere(_.errorType == dispatchWarehouseExciseIDInvalid2), request.userAnswers.submissionFailures.indexWhere(_.errorType == dispatchWarehouseExciseIDConsignorLinkError))
}
