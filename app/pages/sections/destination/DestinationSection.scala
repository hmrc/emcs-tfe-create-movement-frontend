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

package pages.sections.destination

import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{DirectDelivery, EuTaxWarehouse, ExemptedOrganisation, GbTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee}
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import utils.JsonOptionFormatter
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object DestinationSection extends Section[JsObject] with JsonOptionFormatter {

  override val path: JsPath = JsPath \ "destination"

  def shouldStartFlowAtDestinationWarehouseExcise(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      GbTaxWarehouse,
      EuTaxWarehouse
    ).contains(destinationTypePageAnswer)

  def shouldStartFlowAtDestinationWarehouseVat(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      RegisteredConsignee,
      TemporaryRegisteredConsignee,
      ExemptedOrganisation
    ).contains(destinationTypePageAnswer)

  def shouldStartFlowAtDestinationBusinessName(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      DirectDelivery
    ).contains(destinationTypePageAnswer)

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(DestinationTypePage) match {
      case Some(value) =>
        implicit val destinationTypePageAnswer: MovementScenario = value
        if(shouldStartFlowAtDestinationWarehouseExcise) {
          startFlowAtDestinationWarehouseExciseStatus
        } else if(shouldStartFlowAtDestinationWarehouseVat) {
          startFlowAtDestinationWarehouseVatStatus
        } else if(shouldStartFlowAtDestinationBusinessName) {
          startFlowAtDestinationBusinessNameStatus
        } else {
          NotStarted
        }
      case None => NotStarted
    }

  private def startFlowAtDestinationWarehouseExciseStatus(implicit request: DataRequest[_]): TaskListStatus =
    (
      request.userAnswers.get(DestinationWarehouseExcisePage),
      request.userAnswers.get(DestinationConsigneeDetailsPage),
      request.userAnswers.get(DestinationBusinessNamePage),
      request.userAnswers.get(DestinationAddressPage)
    ) match {
      case (Some(_), Some(true), _, _) => Completed
      case (Some(_), Some(false), Some(_), Some(_)) => Completed
      case (Some(_), Some(false), bn, a) if bn.isEmpty || a.isEmpty => InProgress
      case (Some(_), _, _, _) => InProgress
      case _ => NotStarted
    }

  private def startFlowAtDestinationWarehouseVatStatus(implicit request: DataRequest[_]): TaskListStatus =
    (
      request.userAnswers.get(DestinationDetailsChoicePage),
      request.userAnswers.get(DestinationConsigneeDetailsPage),
      request.userAnswers.get(DestinationBusinessNamePage),
      request.userAnswers.get(DestinationAddressPage)
    ) match {
      case (Some(false), _, _, _) => Completed
      case (Some(true), Some(true), _, _) => Completed
      case (Some(true), Some(false), Some(_), Some(_)) => Completed
      case (Some(_), Some(false), bn, a) if bn.isEmpty || a.isEmpty => InProgress
      case (Some(_), _, _, _) => InProgress
      case _ if request.userAnswers.get(DestinationWarehouseVatPage).nonEmpty => InProgress
      case _ => NotStarted
    }

  private def startFlowAtDestinationBusinessNameStatus(implicit request: DataRequest[_]): TaskListStatus =
    (
      request.userAnswers.get(DestinationBusinessNamePage),
      request.userAnswers.get(DestinationAddressPage)
    ) match {
      case (Some(_), Some(_)) => Completed
      case (bn, a) if bn.isEmpty && a.isEmpty => NotStarted
      case _ => InProgress
    }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(DestinationTypePage) match {
      case Some(value) if Seq(
        GbTaxWarehouse,
        EuTaxWarehouse,
        RegisteredConsignee,
        TemporaryRegisteredConsignee,
        ExemptedOrganisation,
        DirectDelivery
      ).contains(value) => true
      case _ => false
    }
}
