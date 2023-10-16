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

package pages.sections.transportUnit

import models.Index
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsArray, JsPath}
import queries.TransportUnitsCount
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object TransportUnitsSection extends Section[JsArray] {
  override val toString: String = "transportUnits"
  override val path: JsPath = JsPath \ toString

  val MAX: Int = 99

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    request.userAnswers.get(TransportUnitsCount) match {
      case Some(count) =>
        val statuses: Seq[TaskListStatus] = Range(0, count).map(value => TransportUnitSection(Index(value)).status)
        if (statuses.forall(_ == NotStarted)) {
          // every transport unit is not started
          NotStarted
        } else if (statuses.filterNot(_ == NotStarted).forall(_ == Completed)) {
          // every transport unit, other than ones which are not started, is completed
          Completed
        } else {
          InProgress
        }
      case _ => NotStarted
    }
  }
}
