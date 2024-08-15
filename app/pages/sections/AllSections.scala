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

package pages.sections

import models.requests.DataRequest
import pages.sections.consignee.ConsigneeSection
import pages.sections.consignor.ConsignorSection
import pages.sections.destination.DestinationSection
import pages.sections.dispatch.DispatchSection
import pages.sections.documents.DocumentsSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.firstTransporter.FirstTransporterSection
import pages.sections.guarantor.GuarantorSection
import pages.sections.importInformation.ImportInformationSection
import pages.sections.info.InfoSection
import pages.sections.items.ItemsSection
import pages.sections.journeyType.JourneyTypeSection
import pages.sections.sad.SadSection
import pages.sections.transportArranger.TransportArrangerSection
import pages.sections.transportUnit.TransportUnitsSection
import play.api.libs.json.{JsObject, JsPath}
import utils.Logging
import viewmodels.taskList._

case object AllSections extends Section[JsObject] with Logging {

  override val path: JsPath = JsPath

  override def status(implicit request: DataRequest[_]): TaskListStatus = Seq(
    InfoSection,
    ConsigneeSection,
    ConsignorSection,
    DestinationSection,
    DispatchSection,
    DocumentsSection,
    ExportInformationSection,
    FirstTransporterSection,
    GuarantorSection,
    ImportInformationSection,
    ItemsSection,
    JourneyTypeSection,
    SadSection,
    TransportArrangerSection,
    TransportUnitsSection
  ).filter(_.canBeCompletedForTraderAndDestinationType).map { section =>
    if(!section.isCompleted) {
      logger.info(s"[status] ${section.getClass.getSimpleName.stripSuffix("$")} is not complete. Has status: '${section.status}'")
    }
    section
  } match {
    case sections if sections.forall(_.isCompleted) => Completed
    case sections if sections.forall(_.status == NotStarted) => NotStarted
    case sections if sections.exists(_.status == UpdateNeeded) => UpdateNeeded
    case _ => InProgress
  }

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
  // $COVERAGE-ON$
}
