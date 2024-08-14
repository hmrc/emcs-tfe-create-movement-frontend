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

package pages.sections.consignor

import models.NorthernIrelandTemporaryCertifiedConsignor
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object ConsignorSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "consignor"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (
      request.userTypeFromErn,
      ConsignorPaidTemporaryAuthorisationCodePage.value,
      ConsignorAddressPage().value
    ) match {
      case (NorthernIrelandTemporaryCertifiedConsignor, Some(_), Some(_)) =>
        Completed
      case (NorthernIrelandTemporaryCertifiedConsignor, ptaCodePage, addressPage) if ptaCodePage.nonEmpty || addressPage.nonEmpty =>
        InProgress
      case (NorthernIrelandTemporaryCertifiedConsignor, None, None) =>
        NotStarted
      case (_, _, None) =>
        NotStarted
      case _ =>
        Completed
    }

  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
