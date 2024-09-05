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

import models.requests.DataRequest
import models.sections.items.ItemsAddToList
import models.{Index, UserAnswers}
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.{ItemsCount, ItemsPackagingCount}
import viewmodels.taskList._

case object ItemsSection extends Section[JsObject] {

  override val path: JsPath = JsPath \ "items"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (request.userAnswers.getCount(ItemsCount), ItemsAddToListPage.value) match {
      case (Some(0) | None, _) => NotStarted
      case (_, Some(ItemsAddToList.No)) =>
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
      ItemPackagingShippingMarksPage(itemIdx, packagingIdx).value
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
      val optionalValue = ItemPackagingShippingMarksPage(itemIdx, packagingIdx).value
      if (optionalValue.contains(valueToMatch)) {
        Seq((Index(itemIdx), Index(packagingIdx)))
      } else {
        Seq()
      }
    }

  /**
   * @param f       takes two indexes and returns an IterableOnce[A]. IterableOnce can be Seq, Option, etc
   * @param request user's DataRequest containing current UserAnswers
   * @tparam A      type for the function to return. Can be String, (Index, Index), etc
   * @return        for every item, for every packaging within that item, perform function f
   */
  private def forEveryPackagingInsideEveryItem[A](f: (Int, Int) => IterableOnce[A])(implicit request: DataRequest[_]): Seq[A] =
    request.userAnswers.getCount(ItemsCount)
      .map {
        itemsCount =>
          (0 until itemsCount)
            .flatMap(itemIdx => request.userAnswers.getCount(ItemsPackagingCount(itemIdx)).map {
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

  def shippingMarkForItemIsUsedOnOtherItems(itemIdx: Index, packageIdx: Index)(implicit request: DataRequest[_]): Boolean =
    (for {
      quantity <- ItemPackagingQuantityPage(itemIdx, packageIdx).value
      shippingMark <- ItemPackagingShippingMarksPage(itemIdx, packageIdx).value
    } yield {
      if (quantity == "0") false else {
        //Check if any items have the same shipping mark and a package quantity of zero
        retrieveShippingMarkLocationsMatching(shippingMark).exists { case (linkedItemIdx, linkedPackageIdx) =>
          ItemPackagingQuantityPage(linkedItemIdx, linkedPackageIdx).value.getOrElse("0") == "0"
        }
      }
    }).getOrElse(false)

  def removeAnyPackagingThatMatchesTheShippingMark(shippingMark: String, excludingIndex: Option[(Index, Index)] = None)
                                                  (implicit request: DataRequest[_]): UserAnswers = {

    val indexesToRemove = excludingIndex match {
      case Some((itemIdx, packageIdx)) => retrieveShippingMarkLocationsMatching(shippingMark).filterNot(_ == (itemIdx, packageIdx))
      case None => retrieveShippingMarkLocationsMatching(shippingMark)
    }

    //Reversing here is important to ensure the indexes are deleted backwards, otherwise indexes before this index would be removed first
    //which would lead to the subsequence indexes being out of sync and an array out of bounds error being thrown
    indexesToRemove.reverse.foldLeft(request.userAnswers) { case (answers, (iIdx, pIdx)) =>
      answers.remove(ItemsPackagingSectionItems(iIdx, pIdx))
    }
  }
}
