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

import models.requests.DataRequest
import models.{Index, MovementSubmissionFailure}
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.IndexedSubmissionFailureHelper.submissionHasItemErrorAtIndex
import utils._

// indexesOfMovementSubmissionErrors is not needed here because when the user
// changes their answer, all the errors for this item are deleted
case class ItemExciseProductCodePage(idx: Index) extends QuestionPage[String] {
  override val toString: String = "itemExciseProductCode"
  override val path: JsPath = ItemsSectionItem(idx).path \ toString

  private val itemExciseProductCodesError: Seq[String] = Seq(
    ItemExciseProductCodeConsignorNotApprovedToSendError.code,
    ItemExciseProductCodeConsigneeNotApprovedToReceiveError.code,
    ItemExciseProductCodeDestinationNotApprovedToReceiveError.code,
    ItemExciseProductCodeDispatchPlaceNotAllowedError.code
  )

  private def isExciseProductCodeAtIndex: MovementSubmissionFailure => Boolean = submissionFailure =>
    submissionHasItemErrorAtIndex(idx, submissionFailure) &&
      itemExciseProductCodesError.contains(submissionFailure.errorType)

  private def getMovementSubmissionFailure(implicit request: DataRequest[_]): Option[MovementSubmissionFailure] =
    request.userAnswers.submissionFailures.find(isExciseProductCodeAtIndex)

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    getMovementSubmissionFailure.exists(!_.hasBeenFixed)

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    getMovementSubmissionFailure.flatMap(_.originalAttributeValue)

  def getSubmissionErrorCodes(isOnAddToList: Boolean)(implicit request: DataRequest[_]): Seq[SubmissionError] =
    request.userAnswers.submissionFailures.collect {
      case error if isExciseProductCodeAtIndex(error) && !error.hasBeenFixed => SubmissionError(error.errorType, idx, isOnAddToList)
    }
}
