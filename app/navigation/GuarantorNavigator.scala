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
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.guarantor._
import play.api.mvc.Call

import javax.inject.Inject

class GuarantorNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case GuarantorRequiredPage => (userAnswers: UserAnswers) =>

      userAnswers.get(GuarantorRequiredPage) match {
        case Some(true) =>
          controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        case _ =>
          controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case GuarantorArrangerPage => (userAnswers: UserAnswers) =>
      userAnswers.get(GuarantorArrangerPage) match {
        case Some(GoodsOwner) | Some(Transporter) =>
          controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        case _ =>
          controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }

    case GuarantorNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case GuarantorVatPage => (userAnswers: UserAnswers) =>
      controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case GuarantorAddressPage => (userAnswers: UserAnswers) =>
      controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case GuarantorCheckAnswersPage => (userAnswers: UserAnswers) =>
      routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case _ =>
      (userAnswers: UserAnswers) =>
        controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) =>
        controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
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
