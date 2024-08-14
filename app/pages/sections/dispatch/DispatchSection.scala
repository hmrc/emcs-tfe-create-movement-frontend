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
import models._
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee}
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList._

case object DispatchSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "dispatch"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {

    val isCertifiedConsigneeType = DestinationTypePage.value.fold(false)(Seq(
      TemporaryCertifiedConsignee,
      CertifiedConsignee
    ).contains(_))

    val hasDispatchWarehouseExciseOrCertifiedConsignee =
      DispatchWarehouseExcisePage.value.isDefined || isCertifiedConsigneeType

    def checkRemainingPages: TaskListStatus = {

      val address = DispatchAddressPage.value.isDefined || !request.isCertifiedConsignor

      (address, isCertifiedConsigneeType) match {
        case (false, true) => NotStarted
        case (true, _) => Completed
        case _ => InProgress
      }
    }

    if (DispatchWarehouseExcisePage.isMovementSubmissionError) UpdateNeeded else {
      if(hasDispatchWarehouseExciseOrCertifiedConsignee) {
        DispatchUseConsignorDetailsPage.value match {
          case Some(true) if DispatchAddressPage.value.nonEmpty => Completed
          case Some(true) => InProgress
          case _ => checkRemainingPages
        }
      } else {
        NotStarted
      }
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
