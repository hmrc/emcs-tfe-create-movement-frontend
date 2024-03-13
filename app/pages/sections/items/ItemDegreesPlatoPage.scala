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
import models.sections.items.ItemDegreesPlatoModel
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.SubmissionFailureErrorCodes.itemDegreesPlatoError

case class ItemDegreesPlatoPage(idx: Index) extends QuestionPage[ItemDegreesPlatoModel] {
  override val toString: String = "itemDegreesPlato"
  override val path: JsPath = ItemsSectionItem(idx).path \ toString

  private def isDegreePlatoErrorAtIndex: MovementSubmissionFailure => Boolean = submissionFailure =>
    submissionFailure.errorLocation.exists(_.contains(s"$BODYEADESAD[${idx.position + 1}]")) && submissionFailure.errorType == itemDegreesPlatoError

  private def getSubmissionError(implicit request: DataRequest[_]): Option[MovementSubmissionFailure] = {
    request.userAnswers.submissionFailures.find(isDegreePlatoErrorAtIndex)
  }

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean = {
    getSubmissionError.exists(!_.hasBeenFixed)
  }

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    getSubmissionError.flatMap(_.originalAttributeValue)

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    Seq(request.userAnswers.submissionFailures.indexWhere(isDegreePlatoErrorAtIndex))
}

