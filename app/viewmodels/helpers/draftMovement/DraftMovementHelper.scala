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

package viewmodels.helpers.draftMovement

import models._
import models.requests.DataRequest
import models.response.{InvalidUserTypeException, MissingMandatoryPage}
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.info.{DestinationTypePage, DispatchPlacePage, InfoSection}
import play.api.i18n.Messages
import play.twirl.api.Html
import utils.Logging
import viewmodels.taskList.{TaskList, TaskListSection, TaskListSectionRow}
import views.html.components.taskList

import javax.inject.Inject

class DraftMovementHelper @Inject()(taskList: taskList) extends Logging {

  // disable for "line too long" warnings
  // noinspection ScalaStyle
  def heading(implicit request: DataRequest[_], messages: Messages): String =
    (request.userTypeFromErn, request.userAnswers.get(DestinationTypePage)) match {
      case (GreatBritainWarehouseKeeper, Some(GbTaxWarehouse)) =>
        messages("draftMovement.heading.gbTaxWarehouseTo", messages(s"destinationType.$GbTaxWarehouse"))

      case (NorthernIrelandWarehouseKeeper, Some(destinationType@(GbTaxWarehouse | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination))) =>
        request.userAnswers.get(DispatchPlacePage) match {
          case Some(value) =>
            messages("draftMovement.heading.dispatchPlaceTo", messages(s"dispatchPlace.$value"), messages(s"destinationType.$destinationType"))
          case None =>
            logger.error(s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper")
            throw MissingMandatoryPage(s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper")
        }

      case (GreatBritainRegisteredConsignor | NorthernIrelandRegisteredConsignor, Some(destinationType)) =>
        messages("draftMovement.heading.importFor", messages(s"destinationType.$destinationType"))

      case (GreatBritainWarehouseKeeper | NorthernIrelandWarehouseKeeper, Some(destinationType@(ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu))) =>
        messages(s"destinationType.$destinationType")

      case (userType, destinationType) =>
        logger.error(s"[heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
        throw InvalidUserTypeException(s"[DraftMovementHelper][heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
    }

  def movementRow(implicit request: DataRequest[_], messages: Messages): TaskListSection = TaskListSection(
    sectionHeading = messages("draftMovement.section.movement"),
    rows = Seq(
      TaskListSectionRow(
        taskName = messages("draftMovement.section.movement.movementDetails"),
        id = "movementDetails",
        link = Some(controllers.sections.info.routes.InfoIndexController.onPageLoad(request.ern, request.draftId).url),
        status = InfoSection.status
      )
    )
  )

  def rows(implicit request: DataRequest[_], messages: Messages): Html = {
    taskList(TaskList(sections = Seq(
      movementRow
    )))
  }

}
