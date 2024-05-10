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

package controllers.sections.consignee

import controllers.BaseNavigationController
import controllers.actions._
import models._
import models.requests.UserRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeSection
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.taskList.UpdateNeeded

import javax.inject.Inject

class ConsigneeIndexController @Inject()(override val messagesApi: MessagesApi,
                                         override val auth: AuthAction,
                                         override val betaAllowList: BetaAllowListAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val navigator: ConsigneeNavigator,
                                         override val userAnswersService: UserAnswersService,
                                         val controllerComponents: MessagesControllerComponents
                                        ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] = {
    authorisedDataRequest(ern, draftId) {
      implicit dataRequest =>
        withAnswer(DestinationTypePage) {
          destinationTypePageAnswer =>
            val ur: UserRequest[_] = dataRequest.request
            if (ConsigneeSection.isCompleted || ConsigneeSection.status == UpdateNeeded) {
              Redirect(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(ern, draftId))
            } else {
              if (shouldStartFlowFromConsigneeExemptOrganisation(destinationTypePageAnswer)) {
                Redirect(controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(ern, draftId, NormalMode))
              } else if (shouldStartFlowFromConsigneeInformation(destinationTypePageAnswer)) {
                Redirect(controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(ern, draftId, NormalMode))
              } else if (shouldStartFlowFromConsigneeExcise(destinationTypePageAnswer)) {
                Redirect(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, draftId, NormalMode))
              } else {
                logger.info(s"[onPageLoad] Combination of UserType ${ur.userTypeFromErn} and" +
                  s" DestinationTypePage answer $destinationTypePageAnswer not allowed on Consignee flow")
                Redirect(controllers.routes.DraftMovementController.onPageLoad(ern, draftId))
              }
            }
        }

    }
  }

  private def shouldStartFlowFromConsigneeInformation(destinationTypePageAnswer: MovementScenario): Boolean = {
    Seq(
      ExportWithCustomsDeclarationLodgedInTheUk,
      ExportWithCustomsDeclarationLodgedInTheEu
    ).contains(destinationTypePageAnswer)
  }

  private def shouldStartFlowFromConsigneeExcise(destinationTypePageAnswer: MovementScenario): Boolean = {
    (UkTaxWarehouse.values ++ Seq(
      EuTaxWarehouse,
      DirectDelivery,
      RegisteredConsignee,
      TemporaryRegisteredConsignee,
      TemporaryCertifiedConsignee,
      CertifiedConsignee
    )).contains(destinationTypePageAnswer)
  }

  private def shouldStartFlowFromConsigneeExemptOrganisation(destinationTypePageAnswer: MovementScenario): Boolean =
    destinationTypePageAnswer == ExemptedOrganisation

}
