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

package controllers.sections.documents

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import navigation.DocumentsNavigator
import pages.sections.documents.DocumentsCheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.helpers.CheckYourAnswersDocumentsHelper
import views.html.sections.documents.DocumentsCheckAnswersView

import javax.inject.Inject

class DocumentsCheckAnswersController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val userAllowList: UserAllowListAction,
                                                 override val navigator: DocumentsNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: DocumentsCheckAnswersView,
                                                 checkAnswersHelper: CheckYourAnswersDocumentsHelper
                                               ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] = authorisedDataRequest(ern, draftId) { implicit request =>
    Ok(view(checkAnswersHelper.summaryList()))
  }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] = authorisedDataRequest(ern, draftId) { implicit request =>
    Redirect(navigator.nextPage(DocumentsCheckAnswersPage, NormalMode, request.userAnswers))
  }
}
