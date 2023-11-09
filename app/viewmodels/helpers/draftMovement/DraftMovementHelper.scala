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
import pages.sections.consignee.ConsigneeSection
import pages.sections.consignor.ConsignorSection
import pages.sections.destination.DestinationSection
import pages.sections.dispatch.DispatchSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.guarantor.GuarantorSection
import pages.sections.importInformation.ImportInformationSection
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

  private[draftMovement] def movementSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = TaskListSection(
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

  //noinspection ScalaStyle
  private[draftMovement] def deliverySection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("draftMovement.section.delivery"),
      rows = Seq(
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.delivery.consignor"),
          id = "consignor",
          link = Some(controllers.sections.consignor.routes.ConsignorIndexController.onPageLoad(request.ern, request.draftId).url),
          status = ConsignorSection.status
        )),
        if (ImportInformationSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.delivery.import"),
            id = "import",
            link = Some(controllers.sections.importInformation.routes.ImportInformationIndexController.onPageLoad(request.ern, request.draftId).url),
            status = ImportInformationSection.status
          ))
        } else {
          None
        },
        if (DispatchSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.delivery.dispatch"),
            id = "dispatch",
            link = Some(controllers.sections.dispatch.routes.DispatchIndexController.onPageLoad(request.ern, request.draftId).url),
            status = DispatchSection.status
          ))
        } else {
          None
        },
        if (ConsigneeSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.delivery.consignee"),
            id = "consignee",
            link = Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(request.ern, request.draftId).url),
            status = ConsigneeSection.status
          ))
        } else {
          None
        },
        if (DestinationSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.delivery.destination"),
            id = "destination",
            link = Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(request.ern, request.draftId).url),
            status = DestinationSection.status
          ))
        } else {
          None
        },
        if (ExportInformationSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.delivery.export"),
            id = "export",
            link = Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(request.ern, request.draftId).url),
            status = ExportInformationSection.status
          ))
        } else {
          None
        }
      ).flatten
    )
  }

  private[draftMovement] def guarantorSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("draftMovement.section.guarantor"),
      rows = Seq(
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.guarantor.guarantor"),
          id = "guarantor",
          link = Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(request.ern, request.draftId).url),
          status = GuarantorSection.status
        ))
      ).flatten
    )
  }

  def rows(implicit request: DataRequest[_], messages: Messages): Html = {
    taskList(TaskList(sections = Seq(
      movementSection,
      deliverySection,
      guarantorSection,
    )))
  }

}
