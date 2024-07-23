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

package pages.sections.exportInformation

import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheEu, ExportWithCustomsDeclarationLodgedInTheUk}
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, NotStarted, TaskListStatus, UpdateNeeded}

case object ExportInformationSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "exportInformation"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    if(ExportCustomsOfficePage.isMovementSubmissionError) {
      UpdateNeeded
    } else if (ExportCustomsOfficePage.value.nonEmpty) {
      Completed
    } else {
      NotStarted
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    DestinationTypePage.value match {
      case Some(value) if Seq(ExportWithCustomsDeclarationLodgedInTheUk, ExportWithCustomsDeclarationLodgedInTheEu).contains(value) => true
      case _ => false
    }
}
