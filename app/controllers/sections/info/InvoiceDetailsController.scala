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

package controllers.sections.info

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.info.InvoiceDetailsFormProvider
import models.{Mode, NormalMode}
import models.requests.DataRequest
import navigation.Navigator
import pages.sections.info.InvoiceDetailsPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import utils.{DateUtils, TimeMachine}
import views.html.sections.info.InvoiceDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class InvoiceDetailsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: InvoiceDetailsFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: InvoiceDetailsView,
                                       timeMachine: TimeMachine
                                     ) extends BaseNavigationController with AuthActionHelper with DateUtils {

  def onPageLoad(ern: String, lrn: String): Action[AnyContent] =
    authorisedDataRequest(ern, lrn) { implicit request =>
      renderView(Ok, fillForm(InvoiceDetailsPage, formProvider()))
    }

  def onSubmit(ern: String, lrn: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future(renderView(BadRequest, formWithErrors)),
        saveAndRedirect(InvoiceDetailsPage, _, NormalMode)
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Result = {
    status(view(
      form = form,
      currentDate = timeMachine.now().toLocalDate.formatDateNumbersOnly(),
      onSubmitCall = controllers.sections.info.routes.InvoiceDetailsController.onSubmit(request.ern, request.lrn),
      skipQuestionCall = navigator.nextPage(InvoiceDetailsPage, NormalMode, request.userAnswers)
    ))
  }
}
