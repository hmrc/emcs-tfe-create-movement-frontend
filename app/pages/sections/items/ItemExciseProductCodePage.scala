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

package pages.sections.items

import config.Constants.BODYEADESAD
import models.requests.DataRequest
import models.{Index, MovementSubmissionFailure}
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.SubmissionFailureErrorCodes._

case class ItemExciseProductCodePage(idx: Index) extends QuestionPage[String] {
  override val toString: String = "itemExciseProductCode"
  override val path: JsPath = ItemsSectionItem(idx).path \ toString

  private val itemExciseProductCodesError: Seq[String] = Seq(
    itemExciseProductCodeConsignorNotApprovedToSendError,
    itemExciseProductCodeConsigneeNotApprovedToReceiveError,
    itemExciseProductCodeDestinationNotApprovedToReceiveError,
    itemExciseProductCodeDispatchPlaceNotAllowed
  )

  private def isExciseProductCodeAtIndex: MovementSubmissionFailure => Boolean = submissionFailure =>
    submissionFailure.errorLocation.exists(_.contains(s"$BODYEADESAD[${idx.position + 1}]")) &&
      itemExciseProductCodesError.contains(submissionFailure.errorType)

  private def getMovementSubmissionFailure(implicit request: DataRequest[_]): Option[MovementSubmissionFailure] =
    request.userAnswers.submissionFailures.find(isExciseProductCodeAtIndex)

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    getMovementSubmissionFailure.exists(!_.hasBeenFixed)

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    getMovementSubmissionFailure.flatMap(_.originalAttributeValue)

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    request.userAnswers.submissionFailures.zipWithIndex.filter(errorToIndex => isExciseProductCodeAtIndex(errorToIndex._1)).map(_._2)

  def getSubmissionErrorCodes(isOnAddToList: Boolean)(implicit request: DataRequest[_]): Seq[ErrorCode] =
    request.userAnswers.submissionFailures.filter(isExciseProductCodeAtIndex).filter(!_.hasBeenFixed).map(error => ErrorCode(error.errorType, idx, isOnAddToList))
}
