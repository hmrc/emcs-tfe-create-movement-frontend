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

import config.AppConfig
import controllers.actions._
import models.NormalMode
import navigation.Navigator
import pages.CheckAnswersPage
import pages.sections.info.DeferredMovementPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.MovementTemplatesService
import viewmodels.helpers.ItemsAddToListHelper
import views.html.CheckYourAnswersView

import javax.inject.Inject

class CheckYourAnswersController @Inject()(override val messagesApi: MessagesApi,
                                           override val auth: AuthAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: Navigator,
                                           view: CheckYourAnswersView,
                                           movementTemplatesService: MovementTemplatesService,
                                           itemsAddToListHelper: ItemsAddToListHelper,
                                           appConfig: AppConfig
                                          ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(DeferredMovementPage(isOnPreDraftFlow = false)) { isDeferred =>
        itemsAddToListHelper.finalCyaSummary().map { itemsSummary =>
          Ok(view(
            routes.CheckYourAnswersController.onSubmit(ern, draftId),
            isDeferred,
            itemsSummary
          ))
        }
      }
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      movementTemplatesService.getList(ern).map {
        case templates if templates.count >= appConfig.maxTemplates =>
          Redirect(controllers.routes.DeclarationController.onPageLoad(ern, draftId))
        case _ =>
          Redirect(navigator.nextPage(CheckAnswersPage, NormalMode, request.userAnswers))
      }
    }
}
