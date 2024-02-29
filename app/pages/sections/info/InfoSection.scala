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

package pages.sections.info

import models.NorthernIrelandWarehouseKeeper
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList._

object InfoSection extends Section[JsObject] {
  override def status(implicit request: DataRequest[_]): TaskListStatus = {

    val requiredPages: Seq[Option[_]] = Seq(
      request.userAnswers.get(DestinationTypePage),
      request.userAnswers.get(DeferredMovementPage()),
      request.userAnswers.get(LocalReferenceNumberPage()),
      request.userAnswers.get(InvoiceDetailsPage()),
      request.userAnswers.get(DispatchDetailsPage())
    )

    if(request.userAnswers.isSubmissionErrorOnPage(LocalReferenceNumberPage())) {
      UpdateNeeded
    } else if (requiredPages.forall(_.nonEmpty)) {
      if (request.userTypeFromErn == NorthernIrelandWarehouseKeeper) {
        if (request.userAnswers.get(DispatchPlacePage).nonEmpty) {
          Completed
        } else {
          InProgress
        }
      } else {
        Completed
      }
    } else if (requiredPages.exists(_.nonEmpty)) {
      InProgress
    } else {
      NotStarted
    }
  }

  override val path: JsPath = JsPath \ "info"

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
