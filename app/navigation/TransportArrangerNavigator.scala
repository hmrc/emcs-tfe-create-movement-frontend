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
import pages.sections.transportArranger.{TransportArrangerNamePage, TransportArrangerPage}
import play.api.mvc.Call

import javax.inject.Inject

class TransportArrangerNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case TransportArrangerPage => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportArrangerPage) match {

        // TODO redirect to CAM-TA02
        case Some(GoodsOwner) | Some(Other) =>
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()

        // TODO redirect to CAM-TA05
        case _ =>
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    }

    case TransportArrangerNamePage => (_: UserAnswers) =>
      // TODO redirect to CAM-TA03
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ =>
      (userAnswers: UserAnswers) => routes.IndexController.onPageLoad(userAnswers.ern)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)

    //TODO update when other modes are added
    case _ =>
      normalRoutes(page)(userAnswers)
  }
}
