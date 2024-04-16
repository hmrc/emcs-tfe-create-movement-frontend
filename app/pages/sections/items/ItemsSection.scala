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
import models.sections.items.ItemsAddToList
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.{ItemsCount, ItemsPackagingCount}
import viewmodels.taskList._

case object ItemsSection extends Section[JsObject] {

  override val path: JsPath = JsPath \ "items"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (request.userAnswers.get(ItemsCount), request.userAnswers.get(ItemsAddToListPage), ItemsSectionItems.isMovementSubmissionError) match {
      case (_, _, true) => UpdateNeeded
      case (Some(0) | None, _, _) => NotStarted
      case (_, Some(ItemsAddToList.No), _) =>
        ItemsSectionItems.status
      case _ => InProgress
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true

  /**
   * @param request user's DataRequest containing current UserAnswers
   * @return        for every item, for every packaging within that item,
   *                return the ItemPackagingShippingMarksPage(itemIdx, packagingIdx) result if it exists
   */
  def retrieveAllShippingMarks()(implicit request: DataRequest[_]): Seq[String] =
    forEveryPackagingInsideEveryItem { (itemIdx, packagingIdx) =>
      request.userAnswers.get(page = ItemPackagingShippingMarksPage(itemIdx, packagingIdx))
    }

  /**
   * @param valueToMatch value to compare against in user answers
   * @param request      user's DataRequest containing current UserAnswers
   * @return             for every item, for every packaging within that item,
   *                     return (Index(itemIdx), Index(packagingIdx)) if that combination's
   *                     ItemPackagingShippingMarksPage matches the valueToMatch parameter
   */
  def retrieveShippingMarkLocationsMatching(valueToMatch: String)(implicit request: DataRequest[_]): Seq[(Index, Index)] =
    forEveryPackagingInsideEveryItem { (itemIdx, packagingIdx) =>
      val optionalValue = request.userAnswers.get(page = ItemPackagingShippingMarksPage(itemIdx, packagingIdx))
      if (optionalValue.contains(valueToMatch)) {
        Seq((Index(itemIdx), Index(packagingIdx)))
      } else {
        Seq()
      }
    }

  /**
   * @param f       takes two indexes and returns an Iterable[A]. Iterable can be Seq, Option, etc
   * @param request user's DataRequest containing current UserAnswers
   * @tparam A      type for the function to return. Can be String, (Index, Index), etc
   * @return        for every item, for every packaging within that item, perform function f
   */
  private def forEveryPackagingInsideEveryItem[A](f: (Int, Int) => IterableOnce[A])(implicit request: DataRequest[_]): Seq[A] =
    request.userAnswers.get(ItemsCount)
      .map {
        itemsCount =>
          (0 until itemsCount)
            .flatMap(itemIdx => request.userAnswers.get(ItemsPackagingCount(itemIdx)).map {
              packagingCount =>
                (0 until packagingCount)
                  .flatMap {
                    packagingIdx =>
                      f(itemIdx, packagingIdx)
                  }
            })
      }
      .map(_.flatten.distinct)
      .getOrElse(Seq())
}
