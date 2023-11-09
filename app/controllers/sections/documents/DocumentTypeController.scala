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

import controllers.actions._
import forms.sections.documents.DocumentTypeFormProvider
import models.Mode
import navigation.DocumentsNavigator
import pages.sections.documents.DocumentTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetDocumentTypesService, UserAnswersService}
import views.html.sections.documents.DocumentTypeView

import javax.inject.Inject

class DocumentTypeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: DocumentsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       getDocumentTypesService: GetDocumentTypesService,
                                       formProvider: DocumentTypeFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DocumentTypeView
                                     ) extends BaseDocumentsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request => for {
        documentTypes <- getDocumentTypesService.getDocumentTypes()
      } yield Ok(view(fillForm(DocumentTypePage, formProvider()), mode, documentTypes))
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => for {
          documentTypes <- getDocumentTypesService.getDocumentTypes()
        } yield Ok(view(formWithErrors, mode, documentTypes)),
        value =>
          saveAndRedirect(DocumentTypePage, value, mode)
      )
    }
}
