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
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeIndexController @Inject()(override val messagesApi: MessagesApi,
                                         override val auth: AuthAction,
                                         override val userAllowList: UserAllowListAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val navigator: ConsigneeNavigator,
                                         override val userAnswersService: UserAnswersService,
                                         val controllerComponents: MessagesControllerComponents
                                        ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String): Action[AnyContent] = {
    authorisedDataRequestAsync(ern, lrn) {
      implicit dataRequest =>
        withAnswer(DestinationTypePage) {
          destinationTypePageAnswer =>
            val ur: UserRequest[_] = dataRequest.request

            if (shouldStartFlowFromConsigneeExemptOrganisation(destinationTypePageAnswer)) {
              Future.successful(Redirect(controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(ern, lrn, NormalMode)))
            } else if (shouldStartFlowFromConsigneeExportUkEu(ur.userTypeFromErn, destinationTypePageAnswer)) {
              Future.successful(Redirect(controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(ern, lrn, NormalMode)))
            } else if (shouldStartFlowFromConsigneeExcise(ur.userTypeFromErn, destinationTypePageAnswer)) {
              Future.successful(Redirect(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, lrn, NormalMode)))
            } else {
              logger.info(s"[onPageLoad] Combination of UserType ${ur.userTypeFromErn} and" +
                s" DestinationTypePage answer $destinationTypePageAnswer not allowed on Consignee flow")
              // TODO: Change redirect location when tasklist is built
              Future.successful(Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad()))
            }
        }

    }
  }

  private def shouldStartFlowFromConsigneeExportUkEu(
                                                      userTypeFromErn: UserType,
                                                      destinationTypePageAnswer: MovementScenario
                                                    ): Boolean = {
    val gbwkAndSpecificDestinationTypes: Boolean =
      userTypeFromErn == GreatBritainWarehouseKeeper && destinationTypePageAnswer == ExportWithCustomsDeclarationLodgedInTheUk

    val xiwkAndSpecificDestinationTypes: Boolean = {
      val validDestinationTypes: Seq[MovementScenario] = Seq(
        RegisteredConsignee,
        ExportWithCustomsDeclarationLodgedInTheUk,
        ExportWithCustomsDeclarationLodgedInTheEu
      )
      userTypeFromErn == NorthernIrelandWarehouseKeeper && validDestinationTypes.contains(destinationTypePageAnswer)
    }

    gbwkAndSpecificDestinationTypes || xiwkAndSpecificDestinationTypes
  }

  private def shouldStartFlowFromConsigneeExcise(
                                                  userTypeFromErn: UserType,
                                                  destinationTypePageAnswer: MovementScenario
                                                ): Boolean = {
    val validDestinationTypesRegardlessOfUserTypes: Boolean =
      Seq(
        GbTaxWarehouse,
        EuTaxWarehouse,
        DirectDelivery
      ).contains(destinationTypePageAnswer)

    val gbrcAndValidDestinationType: Boolean =
      userTypeFromErn == GreatBritainRegisteredConsignor && destinationTypePageAnswer == ExportWithCustomsDeclarationLodgedInTheUk

    val xircAndValidDestinationType: Boolean = {
      val validDestinationTypes: Seq[MovementScenario] = Seq(
        RegisteredConsignee,
        TemporaryRegisteredConsignee,
        ExportWithCustomsDeclarationLodgedInTheUk,
        ExportWithCustomsDeclarationLodgedInTheEu
      )

      userTypeFromErn == NorthernIrelandRegisteredConsignor && validDestinationTypes.contains(destinationTypePageAnswer)
    }

    val xiwkAndRegisteredConsignee: Boolean =
      (userTypeFromErn == NorthernIrelandWarehouseKeeper) && (destinationTypePageAnswer == TemporaryRegisteredConsignee)

    validDestinationTypesRegardlessOfUserTypes || gbrcAndValidDestinationType || xircAndValidDestinationType || xiwkAndRegisteredConsignee
  }

  private def shouldStartFlowFromConsigneeExemptOrganisation(destinationTypePageAnswer: MovementScenario): Boolean =
    destinationTypePageAnswer == ExemptedOrganisation

}