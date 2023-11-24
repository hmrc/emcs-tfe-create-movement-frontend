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
import models.GoodsTypeModel.GoodsType
import models.requests.DataRequest
import models.{GoodsTypeModel, Index}
import pages.sections.items.{ItemExciseProductCodePage, ItemSelectPackagingPage}
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

  def validatePackagingIndex(itemsIndex: Index, packagingIndex: Index)(onSuccess: => Result)(implicit request: DataRequest[_]): Result = {
    super.validateIndex(ItemsPackagingCount(itemsIndex), packagingIndex)(
      onSuccess,
      Redirect(controllers.sections.items.routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, itemsIndex))
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
        f(GoodsTypeModel.apply(epc))
      case None =>
        Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId))
    }

  def withGoodsTypeAsync(idx: Index)(f: GoodsType => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        f(GoodsTypeModel.apply(epc))
      case None =>
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }
  }

  def withItemPackaging(itemIdx: Index, packagingIdx: Index)(f: String => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(ItemSelectPackagingPage(itemIdx, packagingIdx)) match {
      case Some(itemPackaging) =>
        f(itemPackaging.description)
      case None =>
        Future.successful(Redirect(routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, itemIdx)))
    }
}
