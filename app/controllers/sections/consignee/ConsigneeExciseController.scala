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
import forms.sections.consignee.ConsigneeExciseFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
import models.{Mode, NorthernIrelandRegisteredConsignor, NorthernIrelandWarehouseKeeper}
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExciseView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExciseController @Inject()(override val messagesApi: MessagesApi,
                                          override val auth: AuthAction,
                                          override val betaAllowList: BetaAllowListAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          override val navigator: ConsigneeNavigator,
                                          override val userAnswersService: UserAnswersService,
                                          formProvider: ConsigneeExciseFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: ConsigneeExciseView
                                         ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) {
      implicit request =>
        Ok(view(
          fillForm(ConsigneeExcisePage, formProvider(isNorthernIrishTemporaryRegisteredConsignee)),
          routes.ConsigneeExciseController.onSubmit(ern, draftId, mode),
          isNorthernIrishTemporaryRegisteredConsignee,
          isNorthernIrishTemporaryCertifiedConsignee
        ))
    }


  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        formProvider(isNorthernIrishTemporaryRegisteredConsignee).bindFromRequest().fold(
          formWithErrors =>
            Future.successful(
              BadRequest(view(
                formWithErrors,
                routes.ConsigneeExciseController.onSubmit(ern, draftId, mode),
                isNorthernIrishTemporaryRegisteredConsignee,
                isNorthernIrishTemporaryCertifiedConsignee
              ))
            ),
          exciseRegistrationNumber =>
            saveAndRedirect(ConsigneeExcisePage, exciseRegistrationNumber, mode)
        )
    }

  private def isNorthernIrish(implicit request: DataRequest[_]) = {
    request.userTypeFromErn match {
      case NorthernIrelandRegisteredConsignor | NorthernIrelandWarehouseKeeper => true
      case _ => false
    }
  }

  private def isNorthernIrishTemporaryRegisteredConsignee(implicit request: DataRequest[_]) = {
    val isTemporaryRegisteredConsignee: Boolean =
      request.userAnswers.get(DestinationTypePage).contains(TemporaryRegisteredConsignee)

    isNorthernIrish && isTemporaryRegisteredConsignee
  }

  private def isNorthernIrishTemporaryCertifiedConsignee(implicit request: DataRequest[_]) = {
    val isTemporaryCertifiedConsignee: Boolean =
      request.userAnswers.get(DestinationTypePage).contains(TemporaryCertifiedConsignee)

    isNorthernIrish && isTemporaryCertifiedConsignee
  }

}
