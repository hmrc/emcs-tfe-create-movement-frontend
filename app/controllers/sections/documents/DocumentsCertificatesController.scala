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
import forms.sections.documents.DocumentsCertificatesFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.DocumentsNavigator
import pages.sections.documents.DocumentsCertificatesPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.documents.DocumentsCertificatesView

import javax.inject.Inject
import scala.concurrent.Future

class DocumentsCertificatesController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val userAllowList: UserAllowListAction,
                                                 override val navigator: DocumentsNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 formProvider: DocumentsCertificatesFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: DocumentsCertificatesView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      renderView(Ok, fillForm(DocumentsCertificatesPage, formProvider()), mode)
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future(renderView(BadRequest, formWithErrors, mode)),
        value =>
          saveAndRedirect(DocumentsCertificatesPage, value, mode)
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      form = form,
      onSubmitCall = controllers.sections.documents.routes.DocumentsCertificatesController.onSubmit(request.ern, request.draftId, mode)
    ))

}
