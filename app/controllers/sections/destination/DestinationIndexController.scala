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

package controllers.sections.destination

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import navigation.DestinationNavigator
import pages.sections.destination.DestinationSection
import pages.sections.info.DestinationTypePage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject

class DestinationIndexController @Inject()(
                                            override val userAnswersService: UserAnswersService,
                                            override val navigator: DestinationNavigator,
                                            override val auth: AuthAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            override val userAllowList: UserAllowListAction,
                                            val controllerComponents: MessagesControllerComponents
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      withAnswer(DestinationTypePage) {
        implicit destinationTypePageAnswer =>
          if(DestinationSection.isCompleted) {
            Redirect(routes.DestinationCheckAnswersController.onPageLoad(ern, draftId))
          } else {
            if (shouldRedirectToDestinationWarehouseExcise) {
              Redirect(routes.DestinationWarehouseExciseController.onPageLoad(ern, draftId, NormalMode))
            } else if (shouldRedirectToDestinationWarehouseVat) {
              Redirect(routes.DestinationWarehouseVatController.onPageLoad(ern, draftId, NormalMode))
            } else {
              logger.info(s"[onPageLoad] Invalid DestinationTypePage answer $destinationTypePageAnswer not allowed on Place of Destination flow")
              // TODO: Change redirect location when tasklist is built
              Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad())
            }
          }
      }
    }

  private def shouldRedirectToDestinationWarehouseExcise(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      GbTaxWarehouse,
      EuTaxWarehouse,
      DirectDelivery
    ).contains(destinationTypePageAnswer)

  private def shouldRedirectToDestinationWarehouseVat(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      RegisteredConsignee,
      TemporaryRegisteredConsignee,
      ExemptedOrganisation
    ).contains(destinationTypePageAnswer)

}
