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
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.CnCodeInformation
import models.{GoodsTypeModel, Index}
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.mvc.Result
import queries.{ItemsCount, ItemsPackagingCount}
import services.GetCnCodeInformationService

import scala.concurrent.Future

trait BaseItemsNavigationController extends BaseNavigationController {

  val cnCodeInformationService: GetCnCodeInformationService

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

  def withGoodsTypeAsync(idx: Index)(f: GoodsType => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        f(GoodsTypeModel.apply(epc))
      case None =>
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }

  def withCnCodeInformation(idx: Index)(f: CnCodeInformation => Result)(implicit request: DataRequest[_]): Future[Result] = {
    (request.userAnswers.get(ItemExciseProductCodePage(idx)), request.userAnswers.get(ItemCommodityCodePage(idx))) match {
      case (Some(epc), Some(commodityCode)) =>
        cnCodeInformationService.getCnCodeInformation(Seq(CnCodeInformationItem(epc, commodityCode))).map { response =>
          response.headOption match {
            case Some((_, cnCodeInfo)) =>
              f(cnCodeInfo)
            case _ =>
              logger.warn(s"[onPageLoad] Could not retrieve CnCodeInformation for item productCode: '$epc' and commodityCode: '$commodityCode'")
              Redirect(controllers.routes.JourneyRecoveryController.onPageLoad())
          }
        }
      case _ =>
        logger.warn(s"[onPageLoad] productCode or commodityCode missing from UserAnswers")
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)))
    }
  }
}
