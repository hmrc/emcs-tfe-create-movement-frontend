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
import pages.sections.documents.DocumentsSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.firstTransporter.FirstTransporterSection
import pages.sections.guarantor.GuarantorSection
import pages.sections.importInformation.ImportInformationSection
import pages.sections.info.{DestinationTypePage, DispatchPlacePage, InfoSection}
import pages.sections.items.ItemsSection
import pages.sections.journeyType.JourneyTypeSection
import pages.sections.sad.SadSection
import pages.sections.transportArranger.TransportArrangerSection
import pages.sections.transportUnit.TransportUnitsSection
import play.api.i18n.Messages
import utils.Logging
import viewmodels.taskList._

import javax.inject.Inject

class DraftMovementHelper @Inject()() extends Logging {

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
        section = Some(InfoSection),
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
          section = Some(ConsignorSection),
          status = ConsignorSection.status
        )),
        if (ImportInformationSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.delivery.import"),
            id = "import",
            link = Some(controllers.sections.importInformation.routes.ImportInformationIndexController.onPageLoad(request.ern, request.draftId).url),
            section = Some(ImportInformationSection),
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
            section = Some(DispatchSection),
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
            section = Some(ConsigneeSection),
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
            section = Some(DestinationSection),
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
            section = Some(ExportInformationSection),
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
          section = Some(GuarantorSection),
          status = GuarantorSection.status
        ))
      ).flatten
    )
  }

  private[draftMovement] def transportSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("draftMovement.section.transport"),
      rows = Seq(
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.transport.journeyType"),
          id = "journeyType",
          link = Some(controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(request.ern, request.draftId).url),
          section = Some(JourneyTypeSection),
          status = JourneyTypeSection.status
        )),
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.transport.transportArranger"),
          id = "transportArranger",
          link = Some(controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.draftId).url),
          section = Some(TransportArrangerSection),
          status = TransportArrangerSection.status
        )),
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.transport.firstTransporter"),
          id = "firstTransporter",
          link = Some(controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(request.ern, request.draftId).url),
          section = Some(FirstTransporterSection),
          status = FirstTransporterSection.status
        )),
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.transport.units"),
          id = "units",
          link = Some(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.draftId).url),
          section = Some(TransportUnitsSection),
          status = TransportUnitsSection.status
        ))
      ).flatten
    )
  }

  private[draftMovement] def itemsSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("draftMovement.section.items"),
      rows = Seq(
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.items.items"),
          id = "items",
          link = Some(controllers.sections.items.routes.ItemsIndexController.onPageLoad(request.ern, request.draftId).url),
          section = Some(ItemsSection),
          status = ItemsSection.status
        ))
      ).flatten
    )
  }


  private[draftMovement] def documentsSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("draftMovement.section.documents"),
      rows = Seq(
        if (SadSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("draftMovement.section.documents.sad"),
            id = "sad",
            link = Some(controllers.sections.sad.routes.SadIndexController.onPageLoad(request.ern, request.draftId).url),
            section = Some(SadSection),
            status = ItemsSection.status
          ))
        } else {
          None
        },
        Some(TaskListSectionRow(
          taskName = messages("draftMovement.section.documents.documents"),
          id = "documents",
          link = Some(controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(request.ern, request.draftId).url),
          section = Some(DocumentsSection),
          status = DocumentsSection.status
        ))
      ).flatten
    )
  }

  def sectionsExceptSubmit(implicit request: DataRequest[_], messages: Messages): Seq[TaskListSection] = Seq(
    movementSection,
    deliverySection,
    guarantorSection,
    transportSection,
    itemsSection,
    documentsSection
  )

  private[draftMovement] def submitSection(sectionsExceptSubmit: Seq[TaskListSection])
                                          (implicit request: DataRequest[_], messages: Messages): TaskListSection = {

    val rows: Seq[TaskListSectionRow] = sectionsExceptSubmit.flatMap(_.rows).filter(_.section.exists(_.canBeCompletedForTraderAndDestinationType))

    val status: TaskListStatus = if (rows.nonEmpty && rows.forall(_.status == Completed)) {
      Completed
    } else {
      CannotStartYet
    }

    TaskListSection(
      sectionHeading = messages("draftMovement.section.submit"),
      rows = Seq(TaskListSectionRow(
        taskName = messages("draftMovement.section.submit.reviewAndSubmit"),
        id = "submit",
        link = if (status == Completed) Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url) else None,
        section = None,
        status = status
      ))
    )
  }

  def sections(implicit request: DataRequest[_], messages: Messages): Seq[TaskListSection] =
    sectionsExceptSubmit :+ submitSection(sectionsExceptSubmit)

}
