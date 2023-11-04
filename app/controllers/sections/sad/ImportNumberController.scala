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

package controllers.sections.sad

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.sad.ImportNumberFormProvider

import javax.inject.Inject
import models.{Index, Mode}
import navigation.SadNavigator
import pages.sections.sad.ImportNumberPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.sad.ImportNumberView

import scala.concurrent.Future

class ImportNumberController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: SadNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: ImportNumberFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ImportNumberView
                                     ) extends BaseSadNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validateIndex(idx) {
          Future.successful(Ok(view(fillForm(ImportNumberPage(idx), formProvider()), idx, mode)))
      }
    }

  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validateIndex(idx) {
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, idx, mode))),
          value =>
            saveAndRedirect(ImportNumberPage(idx), value, mode)
        )
      }
    }

}
