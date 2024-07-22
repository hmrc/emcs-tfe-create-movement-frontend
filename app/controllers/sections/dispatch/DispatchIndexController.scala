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

package controllers.sections.dispatch

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee}
import navigation.DispatchNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.DispatchSection
import pages.sections.info.DestinationTypePage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.taskList.UpdateNeeded

import javax.inject.Inject

class DispatchIndexController @Inject()(
                                         override val userAnswersService: UserAnswersService,
                                         override val navigator: DispatchNavigator,
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val betaAllowList: BetaAllowListAction,
                                         val controllerComponents: MessagesControllerComponents
                                       ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      if (DispatchSection.isCompleted || DispatchSection.status  == UpdateNeeded) {
        Redirect(routes.DispatchCheckAnswersController.onPageLoad(ern, draftId))
      } else {
        withAnswer(DestinationTypePage) {
          case CertifiedConsignee | TemporaryCertifiedConsignee =>
            if(ConsignorAddressPage.value.nonEmpty) {
              Redirect(routes.DispatchUseConsignorDetailsController.onPageLoad(ern, draftId, NormalMode))
            } else {
              Redirect(routes.DispatchBusinessNameController.onPageLoad(ern, draftId, NormalMode))
            }
          case _ =>
            Redirect(routes.DispatchWarehouseExciseController.onPageLoad(ern, draftId, NormalMode))
        }
      }
    }
}
