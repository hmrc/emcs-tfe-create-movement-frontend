/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.sections.templates

import config.AppConfig
import controllers.BaseNavigationController
import controllers.actions._
import forms.SaveTemplateFormProvider
import models.Mode
import navigation.Navigator
import pages.SaveTemplatePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.SaveTemplateView

import javax.inject.Inject
import scala.concurrent.Future

class SaveTemplateController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: SaveTemplateFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: SaveTemplateView,
                                     )(implicit val appConfig: AppConfig) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      ifCanUseDraftTemplates(ern, draftId){
        Future(Ok(view(fillForm(SaveTemplatePage, formProvider()), controllers.sections.templates.routes.SaveTemplateController.onSubmit(ern, draftId), mode)))
      }
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, controllers.sections.templates.routes.SaveTemplateController.onSubmit(ern, draftId), mode))),
        {
          case true =>
            Future(Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad()))
          case false =>
            Future(Redirect(controllers.routes.DeclarationController.onPageLoad(ern, draftId)))
        }
      )
    }
}
