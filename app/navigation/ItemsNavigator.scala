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

import controllers.sections.items.routes
import models.GoodsTypeModel._
import models._
import pages.Page
import pages.sections.items._
import play.api.mvc.Call

import javax.inject.Inject

class ItemsNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) => epcRouting(idx, answers, NormalMode)

    case ItemBrandNamePage(_) => (_: UserAnswers) =>
      //TODO: Update to route to next page in flow when built
     testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case ItemAlcoholStrengthPage(idx) => (userAnswers: UserAnswers) =>
      alcoholStrengthRouting(idx, userAnswers)

    case _ =>
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case ItemExciseProductCodePage(idx) => (answers: UserAnswers) => epcRouting(idx, answers, CheckMode)
    case _ =>
      // TODO: update to Items CYA when built
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
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
              //TODO: Redirect to CAM-ITM07
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            } else if (Seq(GreatBritainRegisteredConsignor, GreatBritainWarehouseKeeper).contains(UserType(userAnswers.ern)) && abv < 8.5) {
              //TODO: Redirect to CAM-ITM41
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            } else {
              //TODO: Redirect to CAM-ITM19
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            }
          case Spirits =>
            //TODO: Redirect to CAM-ITM08
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case _ =>
            //TODO: Redirect to CAM-ITM09
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      case _ =>
        routes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

  private def epcRouting(idx: Index, userAnswers: UserAnswers, mode: Mode): Call =
    userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case Some("S500" | "T300" | "S400" | "E600" | "E800" | "E910") =>
        //TODO: Route to CAM-ITM43 when implemented
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      case Some(_) =>
        //TODO: Route to CAM-ITM38 when implemented
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      case _ =>
        controllers.sections.items.routes.ItemsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }

}
