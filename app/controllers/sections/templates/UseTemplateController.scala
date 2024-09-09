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

package controllers.sections.templates

import config.AppConfig
import controllers.BaseController
import controllers.actions._
import controllers.actions.predraft.{PreDraftAuthActionHelper, PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import forms.sections.templates.UseTemplateFormProvider
import play.api.i18n.MessagesApi
import play.api.mvc._
import views.html.sections.templates.UseTemplateView

import javax.inject.Inject

class UseTemplateController @Inject()(override val messagesApi: MessagesApi,
                                      val auth: AuthAction,
                                      val getPreDraftData: PreDraftDataRetrievalAction,
                                      val requirePreDraftData: PreDraftDataRequiredAction,
                                      formProvider: UseTemplateFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: UseTemplateView,
                                      appConfig: AppConfig) extends PreDraftAuthActionHelper with BaseController {

  def onPageLoad(ern: String): Action[AnyContent] =
    authorisedWithPreDraftData(ern) { implicit request =>
      Ok(view(
        formProvider(),
        controllers.sections.templates.routes.UseTemplateController.onSubmit(request.ern)
      ))
    }

  def onSubmit(ern: String): Action[AnyContent] =
    authorisedWithPreDraftData(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          BadRequest(view(formWithErrors, controllers.sections.templates.routes.UseTemplateController.onSubmit(request.ern))),
        {
          case true =>
            Redirect(appConfig.emcsTfeTemplatesUrl(ern))
          case false =>
            Redirect(controllers.sections.info.routes.InfoIndexController.onPreDraftPageLoad(ern))
        }
      )
    }
}
