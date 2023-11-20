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

import controllers.actions._
import handlers.ErrorHandler
import models.NormalMode
import navigation.Navigator
import pages.DeclarationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.Logging
import views.html.DeclarationView

import java.time.LocalDateTime
import javax.inject.Inject

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val auth: AuthAction,
                                       override val userAllowList: UserAllowListAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       val userAnswersService: UserAnswersService,
                                       val navigator: Navigator,
                                       view: DeclarationView,
                                       errorHandler: ErrorHandler
                                     ) extends BaseNavigationController with I18nSupport with AuthActionHelper with Logging {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      Ok(view(submitAction = routes.DeclarationController.onSubmit(ern, draftId)))
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      saveAndRedirect(DeclarationPage, LocalDateTime.now(), NormalMode)
    }
}
