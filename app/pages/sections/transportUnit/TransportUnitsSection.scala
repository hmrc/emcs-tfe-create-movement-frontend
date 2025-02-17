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

import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import models.sections.transportUnit.TransportUnitsAddToListModel
import pages.sections.Section
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.libs.json.{JsObject, JsPath}
import queries.TransportUnitsCount
import viewmodels.taskList.{Completed, InProgress, TaskListStatus}

case object TransportUnitsSection extends Section[JsObject] {
  override val toString: String = "transportUnits"
  override val path: JsPath = JsPath \ toString
  val MAX: Int = 99

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (TransportUnitsSectionUnits.status,
      TransportUnitsAddToListPage.value,
      request.userAnswers.getCount(TransportUnitsCount)) match {
      case (_, _, Some(MAX)) => Completed
      case (Completed, Some(TransportUnitsAddToListModel.NoMoreToCome), _) => Completed
      case (Completed, Some(TransportUnitsAddToListModel.MoreToCome) | None, _) => InProgress
      case (Completed, Some(TransportUnitsAddToListModel.Yes) | None, _) => InProgress
      case (status, _, _) => status
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true

  def onlyFixedTransport(implicit request: DataRequest[_]): Boolean =
    (HowMovementTransportedPage.is(FixedTransportInstallations) || HowMovementTransportedPage.isEmpty) &&
      TransportUnitsSectionUnits.onlyContainsOrIsEmpty(FixedTransport)
}
