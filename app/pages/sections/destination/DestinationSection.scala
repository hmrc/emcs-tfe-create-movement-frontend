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
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import utils.JsonOptionFormatter
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus, UpdateNeeded}

case object DestinationSection extends Section[JsObject] with JsonOptionFormatter {

  override val path: JsPath = JsPath \ "destination"

  def shouldStartFlowAtDestinationWarehouseExcise(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    (UkTaxWarehouse.values ++ Seq(
      EuTaxWarehouse
    )).contains(destinationTypePageAnswer)

  def shouldStartFlowAtDestinationWarehouseVat(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      TemporaryRegisteredConsignee,
      CertifiedConsignee,
      TemporaryCertifiedConsignee,
      ExemptedOrganisation
    ).contains(destinationTypePageAnswer)

  def shouldStartFlowAtDestinationAddress(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      DirectDelivery
    ).contains(destinationTypePageAnswer)

  def shouldSkipDestinationDetailsChoice(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      CertifiedConsignee,
      TemporaryCertifiedConsignee
    ).contains(destinationTypePageAnswer)

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    if (DestinationWarehouseExcisePage.isMovementSubmissionError) {
      UpdateNeeded
    } else {
      DestinationTypePage.value match {
        case Some(value) =>
          implicit val destinationTypePageAnswer: MovementScenario = value
          if (shouldStartFlowAtDestinationWarehouseExcise) {
            startFlowAtDestinationWarehouseExciseStatus
          } else if (shouldStartFlowAtDestinationWarehouseVat) {
            startFlowAtDestinationWarehouseVatStatus
          } else if (shouldStartFlowAtDestinationAddress) {
            startFlowAtDestinationBusinessNameStatus
          } else {
            NotStarted
          }
        case None => NotStarted
      }
    }
  }

  private def startFlowAtDestinationWarehouseExciseStatus(implicit request: DataRequest[_]): TaskListStatus =
    (
      DestinationWarehouseExcisePage.value,
      DestinationAddressPage.value
    ) match {
      case (Some(_), Some(_)) => Completed
      case (None, None) => NotStarted
      case _ => InProgress
    }

  private def startFlowAtDestinationWarehouseVatStatus(implicit request: DataRequest[_], destinationTypePageAnswer: MovementScenario): TaskListStatus = {

    val destinationDetailsChoice = if (shouldSkipDestinationDetailsChoice) Some(true) else {
      DestinationDetailsChoicePage.value
    }

    (
      destinationDetailsChoice,
      DestinationAddressPage.value
    ) match {
      case (Some(false), _) => Completed
      case (Some(_), Some(_)) => Completed
      case _ if DestinationWarehouseVatPage.value.nonEmpty => InProgress
      case (_, None) => NotStarted
      case _ => InProgress
    }
  }

  private def startFlowAtDestinationBusinessNameStatus(implicit request: DataRequest[_]): TaskListStatus = {
    (
      DestinationAddressPage.value
    ) match {
      case Some(_) => Completed
      case None => NotStarted
      case _ => InProgress
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    DestinationTypePage.value.exists {
      movementScenario =>
        (UkTaxWarehouse.values ++ Seq(
          EuTaxWarehouse,
          TemporaryRegisteredConsignee,
          CertifiedConsignee,
          TemporaryCertifiedConsignee,
          ExemptedOrganisation,
          DirectDelivery
        )).contains(movementScenario)
    }

}

