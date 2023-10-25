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

import config.SessionKeys.DEFERRED_MOVEMENT
import controllers.BaseController
import controllers.actions._
import forms.sections.info.DeferredMovementFormProvider
import models.NormalMode
import models.requests.UserRequest
import navigation.InfoNavigator
import pages.sections.info.DeferredMovementPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import views.html.sections.info.DeferredMovementView

import javax.inject.Inject

class DeferredMovementController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            val navigator: InfoNavigator,
                                            override val auth: AuthAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            formProvider: DeferredMovementFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: DeferredMovementView,
                                            val userAllowList: UserAllowListAction
                                          ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      renderView(Ok, formProvider())
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors),
        value =>
          Redirect(navigator.nextPage(DeferredMovementPage, NormalMode, ern))
            .addingToSession(DEFERRED_MOVEMENT -> value.toString)
      )
    }

  def renderView(status: Status, form: Form[_])(implicit request: UserRequest[_]): Result =
    status(view(form, controllers.sections.info.routes.DeferredMovementController.onSubmit(request.ern)))
}
