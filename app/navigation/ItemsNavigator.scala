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
import models.GoodsTypeModel._
import models._
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import pages.Page
import pages.sections.items._
import play.api.mvc.Call

import javax.inject.Inject


class ItemsNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) => epcRouting(idx, answers, NormalMode)

    case ItemCommodityCodePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemConfirmCommodityCodeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)

    case ItemConfirmCommodityCodePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemBrandNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemBrandNamePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.CommercialDescriptionController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemAlcoholStrengthPage(idx) => (userAnswers: UserAnswers) =>
      alcoholStrengthRouting(idx, userAnswers)

    case CommercialDescriptionPage(idx) => (userAnswers: UserAnswers) =>
      commercialDescriptionRouting(idx, userAnswers)

    case ItemMaturationPeriodAgePage(idx) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case ItemGeographicalIndicationChoicePage(idx) => (userAnswers: UserAnswers) =>
      geographicalIndicationChoiceRouting(idx, userAnswers)

    case ItemGeographicalIndicationPage(idx) => (userAnswers: UserAnswers) =>
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
          userAnswers.get(ItemExciseProductCodePage(idx)).map(GoodsTypeModel.apply) match {
            case Some(Wine) =>
              //TODO: Redirect to CAM-ITM15
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            case _ =>
              itemsRoutes.ItemsPackagingIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx)
          }
      }

    case ItemSelectPackagingPage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      itemsRoutes.ItemPackagingQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, itemsIndex, itemsPackagingIndex, NormalMode)

    case ItemPackagingQuantityPage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      //TODO: redirect to CAM-ITM26
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

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
          //TODO: redirect to CAM-ITM28
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        case _ => itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case ItemBulkPackagingSealChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemBulkPackagingSealChoicePage(idx)) match {
        case Some(true) =>
          //TODO: redirect to CAM-ITM29 (bulk packaging version)
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        case _ =>
          //TODO: redirect to CAM-ITM40
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case ItemPackagingSealChoicePage(itemsIndex, itemsPackagingIndex) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemPackagingSealChoicePage(itemsIndex, itemsPackagingIndex)) match {
        case Some(true) =>
          //TODO: redirect to CAM-ITM29 (item packaging version)
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        case _ =>
          //TODO: redirect to CAM-ITM36
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case _ =>
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) => epcRouting(idx, answers, CheckMode)
    case ItemWineMoreInformationChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(ItemWineMoreInformationChoicePage(idx)) match {
        case Some(true) =>
          itemsRoutes.ItemWineMoreInformationController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, CheckMode)
        case _ =>
        // TODO: update to Items CYA when built
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    case _ =>
      // TODO: update to Items CYA when built
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) =>
      answers.get(ItemCommodityCodePage(idx)) match {
        case Some(_) => itemsRoutes.ItemConfirmCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx)
        case None => itemsRoutes.ItemCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx, ReviewMode)
      }
    case ItemCommodityCodePage(idx) => (answers: UserAnswers) =>
      itemsRoutes.ItemConfirmCommodityCodeController.onPageLoad(answers.ern, answers.draftId, idx)
    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
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
        GoodsTypeModel(epc) match {
          case Beer =>
            if (Seq(NorthernIrelandRegisteredConsignor, NorthernIrelandWarehouseKeeper).contains(UserType(userAnswers.ern))) {
              itemsRoutes.ItemDegreesPlatoController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else if (Seq(GreatBritainRegisteredConsignor, GreatBritainWarehouseKeeper).contains(UserType(userAnswers.ern)) && abv < 8.5) {
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else {
              itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            }
          case Spirits =>
            itemsRoutes.ItemMaturationPeriodAgeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case _ =>
            itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
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

  private def commercialDescriptionRouting(idx: Index, userAnswers: UserAnswers): Call =
    userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        GoodsTypeModel(epc) match {
          case Beer | Spirits | Wine | Intermediate =>
            itemsRoutes.ItemAlcoholStrengthController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case Tobacco =>
            itemsRoutes.ItemFiscalMarksChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case Energy if Seq("E470", "E500", "E600", "E930").contains(epc) =>
            itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case Energy =>
            //TODO: Redirect to CAM-ITM33
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case _ =>
            itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        }
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

  private def geographicalIndicationChoiceRouting(idx: Index, userAnswers: UserAnswers): Call =
    (userAnswers.get(ItemGeographicalIndicationChoicePage(idx)),
      userAnswers.get(ItemAlcoholStrengthPage(idx)),
      userAnswers.get(ItemExciseProductCodePage(idx))) match {
      case (Some(geographicalIndicationType), Some(alcoholStrength), Some(epc)) =>
        geographicalIndicationType match {
          case NoGeographicalIndication =>
            val goodsType = GoodsTypeModel(epc)
            val acceptableGoodsTypes = Seq(Spirits, Wine, Intermediate)
            if (acceptableGoodsTypes.contains(goodsType) && alcoholStrength < 8.5) {
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else if (acceptableGoodsTypes.contains(goodsType) && alcoholStrength >= 8.5) {
              itemsRoutes.ItemQuantityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
            } else {
              itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
            }
          case _ =>
            itemsRoutes.ItemGeographicalIndicationController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        }
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

  private def bulkPackagingSelectRouting(idx: Index, userAnswers: UserAnswers): Call =
    userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some(epc) =>
        GoodsTypeModel(epc) match {
          case Wine =>
            userAnswers.get(ItemQuantityPage(idx)) match {
              case Some(quantity) =>
                if (quantity < 60) {
                  //TODO: Redirect to CAM-ITM15
                  testOnly.controllers.routes.UnderConstructionController.onPageLoad()
                } else {
                  //TODO: Redirect to CAM-ITM12
                  testOnly.controllers.routes.UnderConstructionController.onPageLoad()
                }
              case _ => itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
            }
          case _ =>
            itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        }
      case _ =>
        itemsRoutes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }
}
