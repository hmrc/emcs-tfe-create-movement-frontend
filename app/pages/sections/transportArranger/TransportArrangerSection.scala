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

package pages.sections.transportArranger

import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger._
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object TransportArrangerSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "transportArranger"

  override def status(implicit request: DataRequest[_]): TaskListStatus = TransportArrangerPage.value match {
    case Some(Consignee) | Some(Consignor) => Completed
    case Some(_) =>
      if (TransportArrangerVatPage.value.nonEmpty && TransportArrangerAddressPage.value.nonEmpty) {
        Completed
      } else {
        InProgress
      }
    case None =>
      // answer not present yet
      NotStarted
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
