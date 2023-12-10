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
import models.sections.items.ItemsAddToList
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.ItemsCount
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object ItemsSectionItems extends Section[JsObject] {

  override val path: JsPath = ItemsSection.path \ "addedItems"
  val MAX: Int = 999

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(ItemsAddToListPage) match {
      case Some(ItemsAddToList.MoreLater) => InProgress
      case _ =>
        request.userAnswers.get(ItemsCount) match {
          case Some(0) | None => NotStarted
          case Some(count) =>
            if ((0 until count).map(ItemsSectionItem(_).status).forall(_ == Completed)) {
              Completed
            } else {
              InProgress
            }
        }
    }

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
