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
        controllers.sections.destination.routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

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
