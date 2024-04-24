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

package navigation

import controllers.sections.items.{routes => itemsRoutes}
import models.GoodsType._
import models._
import models.sections.items.ItemWineProductCategory.ImportedWine
import models.sections.items.{ItemsAddToList, ItemsPackagingAddToList}
import pages.Page
import pages.sections.items._
import play.api.mvc.Call
import queries.{ItemsCount, ItemsPackagingCount}
import utils.{CommodityCodeHelper, ExciseProductCodeHelper}

import javax.inject.Inject


class ItemsNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) => epcRouting(idx, answers, NormalMode)

    case ItemCommodityCodePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemConfirmCommodityCodeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)

    case ItemConfirmCommodityCodePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemBrandNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemBrandNamePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemCommercialDescriptionController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemAlcoholStrengthPage(idx) => (userAnswers: UserAnswers) =>
      alcoholStrengthRouting(idx, userAnswers)

    case ItemCommercialDescriptionPage(idx) => (userAnswers: UserAnswers) =>
      itemCommercialDescriptionRouting(idx, userAnswers)

    case ItemDensityPage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemMaturationPeriodAgePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemDesignationOfOriginController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemDesignationOfOriginPage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemAlcoholStrengthPage(idx)) match {
        case Some(abv) if abv < 8.5 =>
          itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case Some(_) =>
          itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ =>
          itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case ItemQuantityPage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemNetGrossMassController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemNetGrossMassPage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemBulkPackagingChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemDegreesPlatoPage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemAlcoholStrengthPage(idx)) match {
        case Some(abv) if abv < 8.5 =>
          itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ =>
          itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
      }

    case ItemSmallIndependentProducerPage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemSmallIndependentProducerPage(idx)) match {
        case Some(true) =>
          itemsRoutes.ItemProducerSizeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ =>
          itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
      }

    case ItemProducerSizePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemFiscalMarksChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemFiscalMarksChoicePage(idx)) match {
        case Some(true) => itemsRoutes.ItemFiscalMarksController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case Some(false) => itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ => itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case ItemFiscalMarksPage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemBulkPackagingSelectPage(idx) => (userAnswers: UserAnswers) =>
      bulkPackagingSelectRouting(idx, userAnswers)

    case ItemBulkPackagingChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemBulkPackagingChoicePage(idx)) match {
        case Some(true) =>
          itemsRoutes.ItemBulkPackagingSelectController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ =>
          userAnswers.get(ItemCommodityCodePage(idx)) match {
            case Some(cnCode) if CommodityCodeHelper.isWineCommodityCode(cnCode) =>
              itemsRoutes.ItemWineProductCategoryController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            case _ =>
              itemsRoutes.ItemsPackagingIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
          }
      }

    case ItemSelectPackagingPage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemPackagingQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)

    case ItemPackagingQuantityPage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemPackagingProductTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)

    case ItemPackagingProductTypePage(itemsIndex, itemsPackagingIndex) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(ItemPackagingProductTypePage(itemsIndex, itemsPackagingIndex)) match {
          case Some(true) =>
            itemsRoutes.ItemPackagingSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)
          case Some(false) =>
            itemsRoutes.ItemPackagingShippingMarksController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)
          case _ =>
            itemsRoutes.ItemsPackagingIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex)
        }

    case ItemWineGrowingZonePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemPackagingShippingMarksPage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemPackagingSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)

    case ItemWineMoreInformationChoicePage(idx) => (userAnswers: UserAnswers) =>
      (userAnswers.get(ItemWineMoreInformationChoicePage(idx)), userAnswers.get(ItemBulkPackagingChoicePage(idx))) match {
        case (Some(true), _) =>
          itemsRoutes.ItemWineMoreInformationController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case (Some(false), Some(false)) =>
          itemsRoutes.ItemsPackagingIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
        case (Some(false), Some(true)) =>
          itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ => itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case ItemWineMoreInformationPage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemBulkPackagingChoicePage(idx)) match {
        case Some(false) =>
          itemsRoutes.ItemsPackagingIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
        case Some(true) =>
          itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ => itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case ItemBulkPackagingSealChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemBulkPackagingSealChoicePage(idx)) match {
        case Some(true) =>
          itemsRoutes.ItemBulkPackagingSealTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ =>
          itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
      }

    case ItemPackagingSealChoicePage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemPackagingSealChoicePage(itemsIndex, itemsPackagingIndex)) match {
        case Some(true) =>
          itemsRoutes.ItemPackagingSealTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)
        case _ =>
          itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex)
      }

    case ItemWineProductCategoryPage(idx) => itemWineProductCategoryRouting(idx, NormalMode)

    case ItemBulkPackagingSealTypePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)

    case ItemPackagingSealTypePage(idx, _) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)

    case ItemWineOperationsChoicePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemWineProductCategoryController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemWineOriginPage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemsPackagingAddToListPage(itemsIdx) =>
      itemPackagingAddToListRouting(itemsIdx)(NormalMode)

    case ItemCheckAnswersPage(_) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case ItemsAddToListPage =>
      itemsAddToListRouting(NormalMode)

    case _ => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) =>
      answers.get(ItemCommodityCodePage(idx)) match {
        case Some(_) => itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)
        case None => itemsRoutes.ItemCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx, NormalMode)
      }

    case ItemCommodityCodePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemBrandNamePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemCommercialDescriptionPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemAlcoholStrengthPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemDegreesPlatoPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemMaturationPeriodAgePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemDensityPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case page@ItemFiscalMarksChoicePage(idx) => (answers: UserAnswers) =>
      if (answers.get(page).contains(true)) {
        itemsRoutes.ItemFiscalMarksController.onPageLoad(answers.ern, answers.draftId, idx, CheckMode)
      } else {
        itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)
      }

    case ItemFiscalMarksPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemDesignationOfOriginPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case page@ItemSmallIndependentProducerPage(idx) => (answers: UserAnswers) =>
      if (answers.get(page).contains(true)) {
        itemsRoutes.ItemProducerSizeController.onPageLoad(answers.ern, answers.draftId, idx, CheckMode)
      } else {
        itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)
      }

    case ItemProducerSizePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemQuantityPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemNetGrossMassPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemWineOperationsChoicePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemWineProductCategoryPage(idx) => itemWineProductCategoryRouting(idx, CheckMode)

    case ItemWineGrowingZonePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemWineOriginPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case page@ItemWineMoreInformationChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(page) match {
        case Some(true) =>
          itemsRoutes.ItemWineMoreInformationController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, CheckMode)
        case _ =>
          itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
      }

    case ItemWineMoreInformationPage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(answers.ern, answers.draftId, idx)

    case ItemBulkPackagingChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemBulkPackagingChoicePage(idx)) match {
        case Some(true) =>
          userAnswers.get(ItemBulkPackagingSelectPage(idx)) match {
            case Some(_) =>
              // answer hasn't changed
              itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
            case _ =>
              // answer has changed
              itemsRoutes.ItemBulkPackagingSelectController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          }
        case _ =>
          (userAnswers.get(ItemWineProductCategoryPage(idx)), userAnswers.get(ItemsPackagingSectionItems(idx, Index(0)))) match {
            case (Some(_), _) | (_, Some(_)) =>
              // answer hasn't changed
              itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
            case _ =>
              // answer has changed
              userAnswers.get(ItemCommodityCodePage(idx)) match {
                case Some(cnCode) if CommodityCodeHelper.isWineCommodityCode(cnCode) =>
                  itemsRoutes.ItemWineProductCategoryController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
                case _ =>
                  itemsRoutes.ItemsPackagingIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
              }
          }
      }

    case page@ItemBulkPackagingSealChoicePage(itemIdx) => (userAnswers: UserAnswers) =>
      userAnswers.get(page) match {
        case Some(true) => itemsRoutes.ItemBulkPackagingSealTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx, CheckMode)
        case _ => itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)
      }

    case ItemBulkPackagingSealTypePage(itemIdx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)

    case ItemBulkPackagingSelectPage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)

    case ItemSelectPackagingPage(itemIdx, _) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)

    case page@ItemPackagingProductTypePage(itemIdx, packageIdx) => (userAnswers: UserAnswers) =>
      userAnswers.get(page) match {
        case Some(false) => itemsRoutes.ItemPackagingShippingMarksController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx, packageIdx, CheckMode)
        case _ => itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)
      }

    case ItemPackagingQuantityPage(itemIdx, _) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)

    case ItemPackagingShippingMarksPage(itemIdx, _) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)

    case page@ItemPackagingSealChoicePage(itemIdx, packageIdx) => (userAnswers: UserAnswers) =>
      userAnswers.get(page) match {
        case Some(true) => itemsRoutes.ItemPackagingSealTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx, packageIdx, CheckMode)
        case _ => itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)
      }

    case ItemPackagingSealTypePage(itemIdx, _) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemsPackagingAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)

    case _ => _ =>
      // TODO: update to Items AddToList when built
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) =>
      answers.get(ItemCommodityCodePage(idx)) match {
        case Some(_) => itemsRoutes.ItemConfirmCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx)
        case None => itemsRoutes.ItemCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx, ReviewMode)
      }
    case ItemCommodityCodePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemConfirmCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx)
    case _ => (userAnswers: UserAnswers) =>
      controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }


  private def alcoholStrengthRouting(idx: Index, userAnswers: UserAnswers): Call =
    (userAnswers.get(ItemExciseProductCodePage(idx)), userAnswers.get(ItemAlcoholStrengthPage(idx))) match {
      case (Some(epc), Some(abv)) =>
        GoodsType(epc) match {
          case Beer =>
            if (Seq(NorthernIrelandRegisteredConsignor, NorthernIrelandWarehouseKeeper, NorthernIrelandCertifiedConsignor, NorthernIrelandTemporaryCertifiedConsignor).contains(UserType(userAnswers.ern))) {
              itemsRoutes.ItemDegreesPlatoController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else if (Seq(GreatBritainRegisteredConsignor, GreatBritainWarehouseKeeper).contains(UserType(userAnswers.ern)) && abv < 8.5) {
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else {
              itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            }
          case Spirits =>
            if (ExciseProductCodeHelper.isSpirituousBeverages(epc)) {
              itemsRoutes.ItemMaturationPeriodAgeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else {
              itemsRoutes.ItemDesignationOfOriginController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            }
          case _ =>
            itemsRoutes.ItemDesignationOfOriginController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        }
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }
  private def epcRouting(idx: Index, userAnswers: UserAnswers, mode: Mode): Call =
    userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(_) =>
        itemsRoutes.ItemCommodityCodeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, mode)
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

  private def itemCommercialDescriptionRouting(idx: Index, userAnswers: UserAnswers): Call =
    userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        GoodsType(epc) match {
          case goodsType if goodsType.isAlcohol =>
            itemsRoutes.ItemAlcoholStrengthController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case Tobacco =>
            itemsRoutes.ItemFiscalMarksChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case Energy if Seq("E470", "E500", "E600", "E930").contains(epc) =>
            itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case Energy =>
            itemsRoutes.ItemDensityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case _ =>
            itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        }
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

  private def bulkPackagingSelectRouting(idx: Index, userAnswers: UserAnswers): Call =
    userAnswers.get(ItemCommodityCodePage(idx)) match {
      case Some(cnCode) if CommodityCodeHelper.isWineCommodityCode(cnCode) =>
        userAnswers.get(ItemQuantityPage(idx)) match {
          case Some(quantity) =>
            if (quantity <= 60) {
              itemsRoutes.ItemWineProductCategoryController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else {
              itemsRoutes.ItemWineOperationsChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            }
          case _ => itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
        }
      case Some(_) =>
        itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

  private def itemPackagingAddToListRouting(itemIdx: Index)(mode: Mode): UserAnswers => Call = (userAnswers: UserAnswers) => {
    userAnswers.get(ItemsPackagingAddToListPage(itemIdx)) match {
      case Some(ItemsPackagingAddToList.Yes) =>
        val nextPackageIdx: Index = userAnswers.get(ItemsPackagingCount(itemIdx)).fold(0)(identity)
        itemsRoutes.ItemSelectPackagingController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx, nextPackageIdx, mode)
      case _ =>
        itemsRoutes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemIdx)
    }
  }

  private def itemWineProductCategoryRouting(idx: Index, mode: Mode): UserAnswers => Call = (userAnswers: UserAnswers) => {
    (userAnswers.get(ItemWineProductCategoryPage(idx)), userAnswers.get(ItemBulkPackagingChoicePage(idx)), userAnswers.get(ItemQuantityPage(idx))) match {
      case (Some(ImportedWine), _, _) =>
        itemsRoutes.ItemWineOriginController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, mode)
      case (Some(_), Some(true), Some(quantity)) if quantity > 60 =>
        itemsRoutes.ItemWineGrowingZoneController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, mode)
      case _ =>
        itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, mode)
    }
  }

  private def itemsAddToListRouting(mode: Mode): UserAnswers => Call = (userAnswers: UserAnswers) => {
    userAnswers.get(ItemsAddToListPage) match {
      case Some(ItemsAddToList.Yes) =>
        val nextIdx: Index = userAnswers.get(ItemsCount).fold(0)(identity)
        itemsRoutes.ItemExciseProductCodeController.onPageLoad(userAnswers.ern, userAnswers.draftId, nextIdx, mode)
      case _ =>
        controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }
  }
}
