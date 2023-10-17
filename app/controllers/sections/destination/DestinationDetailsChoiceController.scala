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
import controllers.actions.{AuthAction, AuthActionHelper, DataRequiredAction, DataRetrievalAction, UserAllowListAction}
import forms.sections.destination.DestinationDetailsChoiceFormProvider
import models.Mode
import navigation.DestinationNavigator
import pages.sections.destination.DestinationDetailsChoicePage
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.destination.DestinationDetailsChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationDetailsChoiceController @Inject()(override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: DestinationNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: DestinationDetailsChoiceFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: DestinationDetailsChoiceView,
                                                   val userAllowList: UserAllowListAction
                                                  ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, lrn) {
      implicit request =>
        request.userAnswers.get(DestinationTypePage) match {
          case Some(movementScenario) =>
            Ok(view(
              form = fillForm(DestinationDetailsChoicePage, formProvider(movementScenario)),
              action = routes.DestinationDetailsChoiceController.onSubmit(ern, lrn, mode),
              movementScenario = movementScenario
            ))
          case None =>
            Redirect(controllers.sections.info.routes.DestinationTypeController.onSubmit(ern))
        }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) {
      implicit request =>
        request.userAnswers.get(DestinationTypePage) match {
          case Some(movementScenario) =>
            formProvider(movementScenario).bindFromRequest().fold(
              formWithErrors =>
                Future.successful(BadRequest(view(formWithErrors, routes.DestinationDetailsChoiceController.onSubmit(ern, lrn, mode), movementScenario))),
              value =>
                saveAndRedirect(DestinationDetailsChoicePage, value, mode)
            )
          case None =>
            Future.successful(Redirect(controllers.sections.info.routes.DestinationTypeController.onSubmit(ern)))
        }
    }
}