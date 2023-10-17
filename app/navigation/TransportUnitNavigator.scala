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
import models.{Mode, NormalMode, UserAnswers}
import pages.sections.transportUnit.{TransportSealChoicePage, TransportSealTypePage}
import pages.{Page, TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.mvc.Call

import javax.inject.Inject

class TransportUnitNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case TransportUnitTypePage => (userAnswers: UserAnswers) =>
      controllers.sections.transportUnit.routes.TransportUnitIdentityController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case TransportUnitIdentityPage => (userAnswers: UserAnswers) =>
      controllers.sections.transportUnit.routes.TransportSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case TransportSealChoicePage => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportSealChoicePage) match {
        case Some(true) =>
          // TODO redirect to CAM-TU04 once built
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        case _ =>
          // TODO redirect to CAM-TU05 once built
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }


    case TransportSealTypePage => (_: UserAnswers) =>
      // TODO redirect to CAM-TU05
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
