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
import forms.sections.templates.UpdateTemplateFormProvider
import navigation.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{MovementTemplatesService, UserAnswersService}
import views.html.sections.templates.UpdateTemplateView

import javax.inject.Inject
import scala.concurrent.Future

class UpdateTemplateController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          override val userAnswersService: UserAnswersService,
                                          override val navigator: Navigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          formProvider: UpdateTemplateFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: UpdateTemplateView,
                                          templatesService: MovementTemplatesService
                                        )(implicit val appConfig: AppConfig) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      ifCanUseDraftTemplates(ern, draftId) {
        Future.successful(Ok(view(
          form = formProvider(),
          submitAction = controllers.sections.templates.routes.UpdateTemplateController.onSubmit(ern, draftId)
        )))
      }
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(
            form = formWithErrors,
            submitAction = controllers.sections.templates.routes.UpdateTemplateController.onSubmit(ern, draftId)
          ))),
        {
          case true if request.userAnswers.createdFromTemplateId.isDefined =>
            templatesService.saveTemplate(
              templateName = request.userAnswers.createdFromTemplateName.get,
              existingIdToUpdate = request.userAnswers.createdFromTemplateId
            ).map { _ =>
              Redirect(controllers.routes.DeclarationController.onPageLoad(ern, draftId))
            }
          case _ =>
            Future.successful(Redirect(controllers.routes.DeclarationController.onPageLoad(ern, draftId)))
        }
      )
    }
}
