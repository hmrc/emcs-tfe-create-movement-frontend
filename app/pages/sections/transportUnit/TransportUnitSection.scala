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

package pages.sections.transportUnit

import models.Index
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case class TransportUnitSection(transportUnitIndex: Index) extends Section[JsObject] {
  override val path: JsPath = TransportUnitsSectionUnits.path \ transportUnitIndex.position

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    val unitTypeAnswer = TransportUnitTypePage(transportUnitIndex).value
    val identityAnswer = TransportUnitIdentityPage(transportUnitIndex).value
    val sealChoiceAnswer = TransportSealChoicePage(transportUnitIndex).value

    (unitTypeAnswer, identityAnswer, sealChoiceAnswer) match {
      case (Some(FixedTransport), _, _) => Completed
      case (Some(_), Some(_), Some(sca)) =>
        if (sealPagesAreCompleted(sca)) {
          Completed
        } else {
          InProgress
        }
      case _ =>
        if (Seq(unitTypeAnswer, identityAnswer, sealChoiceAnswer).exists(_.nonEmpty)) {
          InProgress
        } else {
          NotStarted
        }
    }
  }

  private def sealPagesAreCompleted(sealChoiceAnswer: Boolean)(implicit request: DataRequest[_]): Boolean =
    TransportSealTypePage(transportUnitIndex).value match {
      case Some(_) if sealChoiceAnswer => true
      case _ if !sealChoiceAnswer => true
      case _ => false
    }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    TransportUnitsSection.canBeCompletedForTraderAndDestinationType
}
