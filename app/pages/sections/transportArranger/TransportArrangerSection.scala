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
import pages.sections.consignee.ConsigneeSection
import pages.sections.consignor.ConsignorSection
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object TransportArrangerSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "transportArranger"

  override def status(implicit request: DataRequest[_]): TaskListStatus = request.userAnswers.get(TransportArrangerPage) match {
    case Some(Consignor) => ConsignorSection.status
    case Some(Consignee) => ConsigneeSection.status
    case Some(_) =>
      if(request.userAnswers.get(TransportArrangerNamePage).nonEmpty &&
        request.userAnswers.get(TransportArrangerVatPage).nonEmpty &&
        request.userAnswers.get(TransportArrangerAddressPage).nonEmpty) {
        Completed
      } else {
        InProgress
      }
    case None =>
      // answer not present yet
      NotStarted
  }
}