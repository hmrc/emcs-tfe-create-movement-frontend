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

import config.SessionKeys.DISPATCH_PLACE
import controllers.BaseController
import controllers.actions._
import forms.sections.info.DispatchPlaceFormProvider
import models.NormalMode
import models.requests.UserRequest
import navigation.InfoNavigator
import pages.sections.info.DispatchPlacePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import views.html.sections.info.DispatchPlaceView

import javax.inject.Inject

class DispatchPlaceController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         val navigator: InfoNavigator,
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         formProvider: DispatchPlaceFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: DispatchPlaceView,
                                         val userAllowList: UserAllowListAction
                                       ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      withNorthernIrelandErn {
        renderView(Ok, formProvider())
      }
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      withNorthernIrelandErn {
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors),
          value =>
            Redirect(navigator.nextPage(DispatchPlacePage, NormalMode, ern))
              .addingToSession(DISPATCH_PLACE -> value.toString)
        )
      }
    }

  def renderView(status: Status, form: Form[_])(implicit request: UserRequest[_]): Result =
    status(view(form, controllers.sections.info.routes.DispatchPlaceController.onSubmit(request.ern)))

  private def withNorthernIrelandErn(f: Result)(implicit request: UserRequest[_]): Result =
    if (request.isNorthernIrelandErn) {
      f
    } else {
      Redirect(controllers.sections.info.routes.DestinationTypeController.onPageLoad(request.ern))
    }
}




