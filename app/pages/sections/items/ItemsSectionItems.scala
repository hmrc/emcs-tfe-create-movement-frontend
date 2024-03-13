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
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.ItemsCount
import viewmodels.taskList._

case object ItemsSectionItems extends Section[JsObject] {

  override val path: JsPath = ItemsSection.path \ "addedItems"
  val MAX: Int = 999

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    (request.userAnswers.get(ItemsCount), isMovementSubmissionError) match {
      case (_, true) => UpdateNeeded
      case (Some(0) | None, _) => NotStarted
      case (Some(count), _) =>
        if ((0 until count).map(ItemsSectionItem(_).status).forall(_ == Completed)) {
          Completed
        } else {
          InProgress
        }
    }

  /**
   * Picks out the index of the submission failure (1-indexed) and returns a list of all distinct occurrences.
   * For example, if the submission failures have:
   *    <code>
   *      <pre>
   *      "errorLocation" : ".../BodyEadEsad[1]/DegreePlato[1]",
   *      ...
   *      "errorLocation" : ".../BodyEadEsad[1]/DegreePlato[1]",
   *      ...
   *      "errorLocation" : ".../BodyEadEsad[2]/DegreePlato[1]",
   *      ...
   *      "errorLocation" : ".../BodyEadEsad[2]/DegreePlato[1]"
   *      </pre>
   *    </code>
   * Then this function will return Seq(1, 2).
   *
   * @return the (distinct) submission failure indexes of items with errors (regardless of them being fixed)
   */
  def indexesOfItemsWithSubmissionFailures(userAnswers: UserAnswers): Seq[Int] =
    userAnswers.submissionFailures
      .collect { case itemError if itemError.errorLocation.exists(_.contains(BODYEADESAD)) =>
        val lookup = s"$BODYEADESAD\\[(\\d+)\\]".r.unanchored
        val lookup(index) = itemError.errorLocation.get
        index.toInt
      }
      .distinct

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean = {
    request.userAnswers.get(ItemsCount) match {
      case Some(0) | None => false
      case Some(count) => (0 until count).exists(ItemsSectionItem(_).isMovementSubmissionError)
    }
  }

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
