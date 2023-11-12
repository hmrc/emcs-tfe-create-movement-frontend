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
import pages.sections.consignee._
import play.api.mvc.Call

import javax.inject.Inject

class ConsigneeNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    // if the [destinationType] was Exempted Organisation
    case ConsigneeExemptOrganisationPage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    // else
    case ConsigneeExportPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ConsigneeExportPage) match {
        case Some(true) =>
          controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        case Some(false) =>
          controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        case _ =>
          controllers.routes.JourneyRecoveryController.onPageLoad()
      }

    case ConsigneeExportVatPage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case ConsigneeExcisePage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case ConsigneeBusinessNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case ConsigneeAddressPage =>
      (userAnswers: UserAnswers) => controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case CheckAnswersConsigneePage =>
      (userAnswers: UserAnswers) => routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }


  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(userAnswers.ern, userAnswers.draftId)
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
