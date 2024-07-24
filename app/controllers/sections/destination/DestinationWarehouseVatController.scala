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
import forms.sections.destination.DestinationWarehouseVatFormProvider
import models.Mode
import navigation.DestinationNavigator
import pages.sections.destination.DestinationWarehouseVatPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.sections.destination.DestinationWarehouseVatView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationWarehouseVatController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: DestinationNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   override val betaAllowList: BetaAllowListAction,
                                                   formProvider: DestinationWarehouseVatFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: DestinationWarehouseVatView
                                                 ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) {
      implicit request =>
        withAnswer(DestinationTypePage) {
          movementScenario =>
            Ok(view(
              form = fillForm(DestinationWarehouseVatPage, formProvider(movementScenario)),
              action = routes.DestinationWarehouseVatController.onSubmit(ern, draftId, mode),
              movementScenario = movementScenario,
              skipQuestionCall = routes.DestinationWarehouseVatController.skipThisQuestion(ern, draftId, mode)
            ))
        }
    }


  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        withAnswerAsync(DestinationTypePage) {
          movementScenario =>
            formProvider(movementScenario).bindFromRequest().fold(
              formWithErrors =>
                Future.successful(BadRequest(view(
                  formWithErrors,
                  routes.DestinationWarehouseVatController.onSubmit(ern, draftId, mode),
                  movementScenario = movementScenario,
                  routes.DestinationWarehouseVatController.skipThisQuestion(ern, draftId, mode)
                ))),
              value =>
                saveAndRedirect(DestinationWarehouseVatPage, value, mode)
            )
        }
    }

  def skipThisQuestion(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      val newUserAnswers = request.userAnswers.remove(DestinationWarehouseVatPage)
      userAnswersService.set(newUserAnswers).map(result => {
        Redirect(navigator.nextPage(DestinationWarehouseVatPage, mode, result))
      }
      )
    }
}

