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
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import models.{Mode, NormalMode, UserAnswers}
import pages.sections.guarantor._
import pages.{GuarantorArrangerPage, Page}
import play.api.mvc.Call

import javax.inject.Inject

class GuarantorNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case GuarantorRequiredPage => (userAnswers: UserAnswers) =>

      userAnswers.get(GuarantorRequiredPage) match {
        case Some(true) =>
          controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
        case _ =>
          // TODO redirect to CAM-GO6 once built
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case GuarantorArrangerPage => (userAnswers: UserAnswers) =>
      userAnswers.get(GuarantorArrangerPage) match {
        case Some(GoodsOwner) | Some(Transporter) =>
          controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
        case _ =>
          // TODO redirect to CAM-GO6 once built
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case GuarantorNamePage => (_: UserAnswers) =>
      // TODO redirect to CAM-GO4 once built
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
