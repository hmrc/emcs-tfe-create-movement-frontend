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
import pages.sections.destination._
import play.api.mvc.Call

import javax.inject.Inject

class DestinationNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case DestinationConsigneeDetailsPage => (userAnswers: UserAnswers) =>
      userAnswers.get(DestinationConsigneeDetailsPage) match {
        case Some(true) =>
          //TODO redirect to CAM-DES06 when built
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        case Some(false) =>
          controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        case _ =>
          controllers.routes.JourneyRecoveryController.onPageLoad()
      }
    case DestinationBusinessNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.destination.routes.DestinationAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case DestinationWarehouseExcisePage =>
      (userAnswers: UserAnswers) =>
        controllers.sections.destination.routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case DestinationAddressPage =>
      //TODO update to next page when finished
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case DestinationDetailsChoicePage =>
      //TODO update to next page when finished
      (userAnswers: UserAnswers) =>
        userAnswers.get(DestinationDetailsChoicePage) match {
          case Some(true) =>
            controllers.sections.destination.routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
          case Some(_) => //TODO update to CAM-02 when built
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case _ => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

        }

    case DestinationWarehouseVatPage =>
      (userAnswers: UserAnswers) =>
        controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case _ =>
      //TODO: update to Destination CYA when built
      (userAnswers: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }


  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      //TODO: update to Destination CYA when built
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
