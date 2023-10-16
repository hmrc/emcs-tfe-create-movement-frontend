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

import controllers.routes
import controllers.sections.transportUnit.{routes => trasnportUnitRoutes}
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.transportUnit._
import play.api.mvc.Call

import javax.inject.Inject

class TransportUnitNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case TransportUnitTypePage(idx) => (userAnswers: UserAnswers) =>
      trasnportUnitRoutes.TransportUnitIdentityController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
    case TransportUnitIdentityPage(idx) => (userAnswers: UserAnswers) =>
      trasnportUnitRoutes.TransportSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case TransportSealChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportSealChoicePage(idx)) match {
        case Some(true) =>
          trasnportUnitRoutes.TransportSealTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
        case _ =>
          trasnportUnitRoutes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
      }

    case TransportSealTypePage(idx) => (userAnswers: UserAnswers) =>
      trasnportUnitRoutes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)

    case TransportUnitGiveMoreInformationChoicePage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(TransportUnitGiveMoreInformationChoicePage(idx)) match {
          case Some(true) =>
            controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
          case _ =>
            //TODO redirect to CAM-TU07
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

    case TransportUnitGiveMoreInformationPage(_) => (_: UserAnswers) =>
      // TODO redirect to CAM-TU07
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ =>
      // TODO: update to CAM-TU07 when built
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      // TODO: update to CAM-TU07 when built
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
