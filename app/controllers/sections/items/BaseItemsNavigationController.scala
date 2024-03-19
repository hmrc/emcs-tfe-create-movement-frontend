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

package controllers.sections.items

import config.Constants.BODYEADESAD
import controllers.BaseNavigationController
import models.requests.DataRequest
import models.{GoodsType, Index, UserAnswers}
import pages.sections.items._
import play.api.mvc.Result
import queries.{ItemsCount, ItemsPackagingCount}
import utils.IndexedSubmissionFailureHelper.submissionHasItemErrorAtIndex

import scala.concurrent.Future

trait BaseItemsNavigationController extends BaseNavigationController {

  def validateIndex(index: Index)(onSuccess: => Result)(implicit request: DataRequest[_]): Result = {
    super.validateIndex(ItemsCount, index)(
      onSuccess,
      Redirect(controllers.sections.items.routes.ItemsIndexController.onPageLoad(request.ern, request.draftId))
    )
  }

  def validateIndexAsync(index: Index)(onSuccess: => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    super.validateIndex(ItemsCount, index)(
      onSuccess,
      Future.successful(Redirect(controllers.sections.items.routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    )
  }

  def validatePackagingIndexAsync(itemsIndex: Index, packagingIndex: Index)(onSuccess: => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    super.validateIndex(ItemsPackagingCount(itemsIndex), packagingIndex)(
      onSuccess,
      Future.successful(Redirect(controllers.sections.items.routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, itemsIndex)))
    )
  }

  def withGoodsType(idx: Index)(f: GoodsType => Result)(implicit request: DataRequest[_]): Result =
    request.userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        f(GoodsType.apply(epc))
      case None =>
        Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId))
    }

  def withGoodsTypeAsync(idx: Index)(f: GoodsType => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        f(GoodsType.apply(epc))
      case None =>
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }

  def withItemBulkPackaging(itemIdx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(ItemBulkPackagingSelectPage(itemIdx)) match {
      case Some(itemPackaging) =>
        f(itemPackaging.description)
      case None =>
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }

  def withItemPackaging(itemIdx: Index, packagingIdx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(ItemSelectPackagingPage(itemIdx, packagingIdx)) match {
      case Some(itemPackaging) =>
        f(itemPackaging.description)
      case None =>
        Future.successful(Redirect(routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, itemIdx)))
    }

  def removingAnyItemsWithoutEPCandCnCode(request: DataRequest[_])(f: DataRequest[_] => Future[Result]): Future[Result] =
    request.userAnswers.get(ItemsCount).fold(f(request)) { count =>

      val indexesOfItemsToBeRemoved = (0 until count).map(Index(_)).collect {
        case idx if request.userAnswers.get(ItemExciseProductCodePage(idx)).isEmpty || request.userAnswers.get(ItemCommodityCodePage(idx)).isEmpty =>
          idx
      }

      if (indexesOfItemsToBeRemoved.isEmpty) f(request) else {

        val updatedAnswers = indexesOfItemsToBeRemoved.foldLeft(request.userAnswers) {
          case (userAnswers, idxToRemove) =>
            userAnswers.remove(ItemsSectionItem(idxToRemove))
        }

        userAnswersService.set(updatedAnswers)(hc(request)).flatMap { _ =>
          f(request.copy(userAnswers = updatedAnswers))
        }
      }
    }

  /**
   * This function first removes the submission failure at the specified index. It then finds the submission failure index (i.e. BodyEadEsad[X]) of every
   * item (that is after this one - if any exist) with errors and then zips the submission failures with an index (so we know the new array index of the submission failure) and finds
   * all errors at the submission failure index (i.e. BodyEadEsad[X]) and maps them to X.
   *
   * Then the new user answers (with the requested error removed) is then iterated over for each error and its error location is shifted down by 1.
   * The tests available in BaseItemsNavigationControllerSpec provide a more detailed view.
   *
   */
  private[controllers] def updateItemSubmissionFailureIndexes(indexOfRemovedItem: Index, userAnswers: UserAnswers): UserAnswers = {
    val userAnswersWithSubmissionFailureRemovedAtIndex = removeItemSubmissionFailure(indexOfRemovedItem, userAnswers)

    val itemIndexesToAmend: Seq[Int] =
      ItemsSectionItems.indexesOfItemsWithSubmissionFailures(userAnswersWithSubmissionFailureRemovedAtIndex)
        .filter(_ > (indexOfRemovedItem.position + 1))

    val indexOfSubmissionFailuresNeedingUpdate =
      itemIndexesToAmend.map { erroredItemIdx =>
        userAnswersWithSubmissionFailureRemovedAtIndex.submissionFailures.zipWithIndex.filter(_._1.errorLocation.exists(_.contains(s"$BODYEADESAD[$erroredItemIdx]"))).map(_._2) -> erroredItemIdx
      }

    indexOfSubmissionFailuresNeedingUpdate.foldLeft(userAnswersWithSubmissionFailureRemovedAtIndex) {
      case (userAnswers, (indexes, erroredIndex)) =>

        indexes.foldLeft(userAnswers) { (userAnswers, index) =>
          val submissionFailureForItem = userAnswers.submissionFailures(index)

          val updatedItem = submissionFailureForItem.copy(errorLocation =
            submissionFailureForItem.errorLocation.map(
              _.replace(s"$BODYEADESAD[$erroredIndex]", s"$BODYEADESAD[${erroredIndex - 1}]")
            )
          )

          userAnswers.copy(submissionFailures = userAnswers.submissionFailures.updated(index, updatedItem))
        }
    }
  }

  private[controllers] def removeItemSubmissionFailure(indexOfRemovedItem: Index, userAnswers: UserAnswers): UserAnswers =
    userAnswers.copy(submissionFailures = userAnswers.submissionFailures.filterNot(submissionHasItemErrorAtIndex(indexOfRemovedItem, _)))
}
