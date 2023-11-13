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
import forms.sections.documents.DocumentsAddToListFormProvider
import models.requests.DataRequest
import models.sections.documents.DocumentsAddToList
import models.{Mode, NormalMode}
import navigation.DocumentsNavigator
import pages.sections.documents.{DocumentsAddToListPage, DocumentsSectionUnits}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.helpers.DocumentsAddToListHelper
import viewmodels.taskList.InProgress
import views.html.sections.documents.DocumentsAddToListView

import javax.inject.Inject
import scala.concurrent.Future

class DocumentsAddToListController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: DocumentsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: DocumentsAddToListFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DocumentsAddToListView,
                                       addToListHelper: DocumentsAddToListHelper
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      renderView(Ok, fillForm(DocumentsAddToListPage, formProvider()))
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future(renderView(BadRequest, formWithErrors)),
        handleSubmissionRedirect(_)
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Result = {
    status(view(
      form = form,
      onSubmitCall = controllers.sections.documents.routes.DocumentsAddToListController.onSubmit(request.ern, request.draftId),
      documents = addToListHelper.allDocumentsSummary(),
      showNoOption = DocumentsSectionUnits.status != InProgress
    ))
  }

  private def handleSubmissionRedirect(answer: DocumentsAddToList)(implicit request: DataRequest[_]): Future[Result] =
    answer match {
      case DocumentsAddToList.Yes =>
        val answersWithDocumentAddToList = request.userAnswers
        userAnswersService.set(request.userAnswers.remove(DocumentsAddToListPage))
          .map { _ =>
            Redirect(navigator.nextPage(
              page = DocumentsAddToListPage,
              mode = NormalMode,
              userAnswers = answersWithDocumentAddToList
            ))
          }
      case value => saveAndRedirect(DocumentsAddToListPage, value, NormalMode)
  }
}
