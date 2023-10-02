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

package controllers.sections.transportArranger

import config.Constants.NONGBVAT
import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.transportArranger.TransportArrangerVatFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportArranger.TransportArrangerVatView

import javax.inject.Inject
import scala.concurrent.Future

class TransportArrangerVatController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: TransportArrangerNavigator,
                                                override val auth: AuthAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                override val userAllowList: UserAllowListAction,
                                                formProvider: TransportArrangerVatFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: TransportArrangerVatView
                                              ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      renderView(Ok, fillForm(TransportArrangerVatPage, formProvider()), mode)
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        saveAndRedirect(TransportArrangerVatPage, _, mode)
      )
    }

  def onNonGbVAT(ern: String, lrn: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      saveAndRedirect(TransportArrangerVatPage, NONGBVAT, NormalMode)
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    withAnswer(TransportArrangerPage) { arranger =>
      Future.successful(status(view(
        form,
        routes.TransportArrangerVatController.onSubmit(request.ern, request.lrn, mode),
        arranger
      )))
    }
}
