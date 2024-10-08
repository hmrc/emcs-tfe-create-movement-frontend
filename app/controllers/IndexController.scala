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

package controllers

import controllers.actions.AuthAction
import models.UserAnswers
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{MovementTemplatesService, PreDraftService, UserAnswersService}

import javax.inject.Inject

class IndexController @Inject()(override val messagesApi: MessagesApi,
                                val preDraftService: PreDraftService,
                                val userAnswersService: UserAnswersService,
                                authAction: AuthAction,
                                val controllerComponents: MessagesControllerComponents,
                                val movementTemplatesService: MovementTemplatesService) extends BaseController {

  def onPageLoad(ern: String): Action[AnyContent] =
    authAction(ern).async { implicit request =>

      val clearPreDraftDraft = preDraftService.set(
        UserAnswers(
          ern = ern,
          draftId = request.sessionId,
          submissionFailures = Seq.empty,
          validationErrors = Seq.empty,
          submittedDraftId = None,
          hasBeenSubmitted = false
        )
      )

      val userHasTemplates = movementTemplatesService.userHasTemplates(ern)

      for {
        _ <- clearPreDraftDraft
        hasTemplates <- userHasTemplates
      } yield {
        if(hasTemplates) {
          Redirect(controllers.sections.templates.routes.UseTemplateController.onPageLoad(ern))
        } else {
          Redirect(controllers.sections.info.routes.InfoIndexController.onPreDraftPageLoad(ern))
        }
      }
    }

}
