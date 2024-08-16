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

import models._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList._

case object DispatchSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "dispatch"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {

    val isDutyPaidMovement = DestinationTypePage.value.fold(false)(MovementScenario.valuesForDutyPaidTraders.contains(_))

    if (DispatchWarehouseExcisePage.isMovementSubmissionError) UpdateNeeded else {
      if (isDutyPaidMovement) {
        dutyPaidStatus
      } else {
        dutySuspendedStatus
      }
    }
  }

  private def dutySuspendedStatus(implicit request: DataRequest[_]): TaskListStatus = {
    (DispatchWarehouseExcisePage.value) match {
      case Some(_: String) => Completed
      case _ => NotStarted
    }
  }

  private def dutyPaidStatus(implicit request: DataRequest[_]): TaskListStatus = {
    (DispatchUseConsignorDetailsPage.value, DispatchAddressPage.value) match {
      case (Some(_: Boolean), Some(_: UserAddress)) => Completed
      case (None, Some(_: UserAddress)) => InProgress
      case (Some(_: Boolean), None) => InProgress
      case _ => NotStarted
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    Seq(
      GreatBritainWarehouseKeeper,
      NorthernIrelandWarehouseKeeper,
      NorthernIrelandCertifiedConsignor,
      NorthernIrelandTemporaryCertifiedConsignor
    ).contains(request.userTypeFromErn)

}
