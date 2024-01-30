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

    val isCertifiedConsigneeType = request.userAnswers.get(DestinationTypePage).fold(false)(Seq(
      TemporaryCertifiedConsignee,
      CertifiedConsignee
    ).contains(_))

    val hasDispatchWarehouseExciseOrCertifiedConsignee =
      request.userAnswers.get(DispatchWarehouseExcisePage).isDefined || isCertifiedConsigneeType

    hasDispatchWarehouseExciseOrCertifiedConsignee match {
      case true => request.userAnswers.get(DispatchUseConsignorDetailsPage) match {
        case Some(true) => Completed
        case Some(false) =>
          val remainingPages: Seq[Option[_]] = Seq(request.userAnswers.get(DispatchBusinessNamePage), request.userAnswers.get(DispatchAddressPage))
          if (remainingPages.forall(_.nonEmpty)) {
            Completed
          } else {
            InProgress
          }
        case None if isCertifiedConsigneeType => NotStarted
        case None => InProgress
      }
      case false => NotStarted
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
