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
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.dispatch.{DispatchAddressPage, DispatchBusinessNamePage, DispatchCheckAnswersPage, DispatchWarehouseExcisePage}
import play.api.mvc.Call

import javax.inject.Inject

class DispatchNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case DispatchWarehouseExcisePage =>
      //TODO: route to CAM-DIS02
      (userAnswers: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case DispatchBusinessNamePage =>
      (userAnswers: UserAnswers) => controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case DispatchAddressPage =>
      (userAnswers: UserAnswers) => controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case DispatchCheckAnswersPage =>
      //TODO update to route to next page when finished
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }


  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRoutes(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
