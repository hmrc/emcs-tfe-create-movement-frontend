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

import config.SessionKeys.INVOICE_DETAILS
import controllers.BaseController
import controllers.actions._
import forms.sections.info.InvoiceDetailsFormProvider
import models.NormalMode
import models.requests.UserRequest
import navigation.InfoNavigator
import pages.sections.info.InvoiceDetailsPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import utils.{DateUtils, TimeMachine}
import views.html.sections.info.InvoiceDetailsView

import javax.inject.Inject

class InvoiceDetailsController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          val navigator: InfoNavigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          override val userAllowList: UserAllowListAction,
                                          formProvider: InvoiceDetailsFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: InvoiceDetailsView,
                                          timeMachine: TimeMachine
                                        ) extends BaseController with AuthActionHelper with DateUtils {

  def onPageLoad(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      renderView(Ok, formProvider())
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => renderView(BadRequest, formWithErrors),
        value => Redirect(navigator.nextPage(InvoiceDetailsPage, NormalMode, ern))
          .addingToSession(INVOICE_DETAILS -> Json.toJson(value).toString())
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: UserRequest[_]): Result = {
    status(view(
      form = form,
      currentDate = timeMachine.now().toLocalDate.formatDateNumbersOnly(),
      onSubmitCall = controllers.sections.info.routes.InvoiceDetailsController.onSubmit(request.ern),
      skipQuestionCall = testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    ))
  }
}
