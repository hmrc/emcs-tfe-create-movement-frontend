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

package controllers.sections.consignee

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.consignee.ConsigneeExportFormProvider
import models.{Mode, NormalMode}
import navigation.ConsigneeNavigator
import pages.sections.consignee.{ConsigneeExportPage, ConsigneeSection}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExportView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExportController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           override val userAnswersService: UserAnswersService,
                                           override val navigator: ConsigneeNavigator,
                                           override val auth: AuthAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           override val userAllowList: UserAllowListAction,
                                           formProvider: ConsigneeExportFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: ConsigneeExportView
                                         ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      Ok(view(fillForm(ConsigneeExportPage, formProvider()), mode))
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          if (request.userAnswers.get(ConsigneeExportPage).contains(value)) {
            Future(Redirect(navigator.nextPage(ConsigneeExportPage, mode, request.userAnswers)))
          } else {

            val cleanedUserAnswers = if (!value) request.userAnswers else request.userAnswers
              .remove(ConsigneeSection)

            saveAndRedirect(
              page = ConsigneeExportPage,
              answer = value,
              currentAnswers = cleanedUserAnswers,
              mode = NormalMode
            )
          }
        }
      )
    }
}
