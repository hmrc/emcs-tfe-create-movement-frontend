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

import config.SessionKeys.DESTINATION_TYPE
import controllers.BaseController
import controllers.actions._
import forms.sections.info.DestinationTypeFormProvider
import models.sections.info.DispatchPlace.GreatBritain
import models.requests.UserRequest
import models.{NormalMode, NorthernIrelandWarehouseKeeper}
import navigation.InfoNavigator
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import views.html.sections.info.DestinationTypeView

import javax.inject.Inject

class DestinationTypeController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           val navigator: InfoNavigator,
                                           override val auth: AuthAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           formProvider: DestinationTypeFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: DestinationTypeView,
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
          Redirect(navigator.nextPage(DestinationTypePage, NormalMode, ern))
            .addingToSession(DESTINATION_TYPE -> value.toString)
      )
    }

  private[info] def renderView(status: Status, form: Form[_])(implicit request: UserRequest[_]): Result = {
    request.dispatchPlace match {
      case _ if request.userTypeFromErn != NorthernIrelandWarehouseKeeper =>
        // GB ERN or XIRC ERN
        status(view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit(request.ern)))
      case Some(dispatchPlace) =>
        // XIWK ERN, dispatchPlace is known
        status(view(dispatchPlace, form, controllers.sections.info.routes.DestinationTypeController.onSubmit(request.ern)))
      case None =>
        // XI ERN, dispatchPlace is unknown
        Redirect(controllers.sections.info.routes.DispatchPlaceController.onPageLoad(request.ern))
    }
  }
}
