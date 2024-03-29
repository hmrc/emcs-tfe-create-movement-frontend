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
import models.sections.items.ItemDegreesPlatoModel
import models.{Index, MovementSubmissionFailure}
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.IndexedSubmissionFailureHelper.submissionHasItemErrorAtIndex
import utils.{ItemDegreesPlatoError, SubmissionError}

case class ItemDegreesPlatoPage(idx: Index) extends QuestionPage[ItemDegreesPlatoModel] {
  override val toString: String = "itemDegreesPlato"
  override val path: JsPath = ItemsSectionItem(idx).path \ toString

  private def isDegreePlatoErrorAtIndex: MovementSubmissionFailure => Boolean = submissionFailure =>
    submissionHasItemErrorAtIndex(idx, submissionFailure) && submissionFailure.errorType == ItemDegreesPlatoError.code

  private def getMovementSubmissionFailure(implicit request: DataRequest[_]): Option[MovementSubmissionFailure] =
    request.userAnswers.submissionFailures.find(isDegreePlatoErrorAtIndex)

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    getMovementSubmissionFailure.exists(!_.hasBeenFixed)

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    getMovementSubmissionFailure.flatMap(_.originalAttributeValue)

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    Seq(request.userAnswers.submissionFailures.indexWhere(isDegreePlatoErrorAtIndex))

  def getSubmissionErrorCode(isOnAddToList: Boolean)(implicit request: DataRequest[_]): Option[SubmissionError] = {
    request.userAnswers.submissionFailures.find(isDegreePlatoErrorAtIndex).filter(!_.hasBeenFixed).map(
      error => SubmissionError(error.errorType, idx, isOnAddToList)
    )
  }
}

