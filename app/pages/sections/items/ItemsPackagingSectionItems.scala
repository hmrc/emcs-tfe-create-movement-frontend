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

package pages.sections.items

import models.Index
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, TaskListStatus}

case class ItemsPackagingSectionItems(itemsIndex: Index, packagingIndex: Index) extends Section[JsObject] {
  override val path: JsPath = ItemsPackagingSection(itemsIndex).path \ packagingIndex.position

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (
      request.userAnswers.get(ItemSelectPackagingPage(itemsIndex, packagingIndex)),
      request.userAnswers.get(ItemPackagingQuantityPage(itemsIndex, packagingIndex)),
      request.userAnswers.get(ItemPackagingShippingMarksChoicePage(itemsIndex, packagingIndex)),
      //TODO: add logic here to guard against user saying yes to shipping marks and then not entering a shipping mark
      request.userAnswers.get(ItemPackagingSealChoicePage(itemsIndex, packagingIndex)),
      request.userAnswers.get(ItemPackagingSealTypePage(itemsIndex, packagingIndex))
    ) match {
      case (Some(_), Some(_), Some(_), Some(false), _) => Completed
      case (Some(_), Some(_), Some(_), Some(true), Some(_)) => Completed
      case _ => InProgress
    }
    //TODO: uncomment in ETFE-3809
//    (
//      request.userAnswers.get(ItemSelectPackagingPage(itemsIndex, packagingIndex)),
//      request.userAnswers.get(ItemPackagingQuantityPage(itemsIndex, packagingIndex)),
//      request.userAnswers.get(ItemPackagingShippingMarksChoicePage(itemsIndex, packagingIndex)),
//      request.userAnswers.get(ItemPackagingShippingMarksPage(itemsIndex, packagingIndex)),
//      request.userAnswers.get(ItemPackagingSealChoicePage(itemsIndex, packagingIndex)),
//      request.userAnswers.get(ItemPackagingSealTypePage(itemsIndex, packagingIndex))
//    ) match {
//      case (Some(_), Some(_), Some(false), _, Some(false), _) => Completed
//      case (Some(_), Some(_), Some(false), _, Some(true), Some(_)) => Completed
//      case (Some(_), Some(_), Some(true), Some(_), Some(true), Some(_)) => Completed
//      case (Some(_), Some(_), Some(true), Some(_), Some(false), _) => Completed
//      case _ => InProgress
//    }
  }

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    ItemsSection.canBeCompletedForTraderAndDestinationType
}
