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

import controllers.BaseNavigationController
import models.requests.DataRequest
import models.{GoodsType, Index}
import pages.sections.items._
import play.api.mvc.Result
import queries.{ItemsCount, ItemsPackagingCount}

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
    ItemExciseProductCodePage(idx).value match {
      case Some(epc) =>
        f(GoodsType.apply(epc))
      case None =>
        Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId))
    }

  def withGoodsTypeAsync(idx: Index)(f: GoodsType => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    ItemExciseProductCodePage(idx).value match {
      case Some(epc) =>
        f(GoodsType.apply(epc))
      case None =>
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }

  def withExciseProductCode(idx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    ItemExciseProductCodePage(idx).value match {
      case Some(epc) => f(epc)
      case None => Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }

  def withItemBulkPackaging(itemIdx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    ItemBulkPackagingSelectPage(itemIdx).value match {
      case Some(itemPackaging) =>
        f(itemPackaging.description)
      case None =>
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }

  def withItemPackaging(itemIdx: Index, packagingIdx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    ItemSelectPackagingPage(itemIdx, packagingIdx).value match {
      case Some(itemPackaging) =>
        f(itemPackaging.description)
      case None =>
        Future.successful(Redirect(routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, itemIdx)))
    }

  def withItemPackagingQuantity(itemsIdx: Index, packagingIdx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    ItemPackagingQuantityPage(itemsIdx, packagingIdx).value match {
      case Some(quantity) => f(quantity)
      case None => Future.successful(Redirect(routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, itemsIdx)))
    }
  }

  def removingAnyItemsWithoutEPCandCnCode(request: DataRequest[_])(f: DataRequest[_] => Future[Result]): Future[Result] =
    request.userAnswers.getCount(ItemsCount).fold(f(request)) { count =>
      implicit val r: DataRequest[_] = request
      val indexesOfItemsToBeRemoved = (0 until count).map(Index(_)).collect {
        case idx if ItemExciseProductCodePage(idx).value.isEmpty || ItemCommodityCodePage(idx).value.isEmpty =>
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
}
