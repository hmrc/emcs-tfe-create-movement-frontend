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
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.sections.transportArranger._
import play.api.mvc.Call

import javax.inject.Inject

class TransportArrangerNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case TransportArrangerPage => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportArrangerPage) match {

        case Some(GoodsOwner) | Some(Other) =>
          controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

        case _ =>
          controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.lrn)
      }

    case TransportArrangerNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case TransportArrangerVatPage => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case TransportArrangerAddressPage => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.lrn)

    case TransportArrangerCheckAnswersPage => _ =>
      //TODO: Update to route to next section when built
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ =>
      (userAnswers: UserAnswers) => routes.IndexController.onPageLoad(userAnswers.ern)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case TransportArrangerPage =>
      normalRoutes(TransportArrangerPage)
    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.lrn)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case _ =>
      checkRoutes(page)(userAnswers)
  }
}
