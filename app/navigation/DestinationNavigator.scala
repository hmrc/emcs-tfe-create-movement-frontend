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

import controllers.sections.destination.routes
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee}
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.destination._
import pages.sections.info.DestinationTypePage
import play.api.mvc.Call

import javax.inject.Inject

class DestinationNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case DestinationWarehouseExcisePage =>
      (userAnswers: UserAnswers) => routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
    case DestinationWarehouseVatPage =>
      destinationWarehouseVatRouting()
    case DestinationDetailsChoicePage =>
      destinationDetailsChoiceRouting()
    case DestinationConsigneeDetailsPage =>
      destinationConsigneeDetailsRouting()
    case DestinationBusinessNamePage =>
      (userAnswers: UserAnswers) => routes.DestinationAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
    case DestinationAddressPage =>
      (userAnswers: UserAnswers) => routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    case DestinationCheckAnswersPage =>
      (userAnswers: UserAnswers) => controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    case _ =>
      (userAnswers: UserAnswers) => routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
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

  private def destinationWarehouseVatRouting(mode: Mode = NormalMode): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DestinationTypePage) match {
      case Some(CertifiedConsignee) | Some(TemporaryCertifiedConsignee) =>
        routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.draftId, mode)
      case _ =>
        routes.DestinationDetailsChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
    }

  private def destinationDetailsChoiceRouting(mode: Mode = NormalMode): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DestinationDetailsChoicePage) match {
      case Some(true) =>
        routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.draftId, mode)
      case Some(_) =>
        routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      case _ =>
        controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  private def destinationConsigneeDetailsRouting(mode: Mode = NormalMode): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DestinationConsigneeDetailsPage) match {
      case Some(true) =>
        routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      case Some(false) =>
        routes.DestinationBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, mode)
      case _ =>
        controllers.routes.JourneyRecoveryController.onPageLoad()
    }
}
