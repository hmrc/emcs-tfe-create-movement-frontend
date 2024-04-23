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

package controllers.sections.dispatch

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.dispatch.DispatchUseConsignorDetailsFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.DispatchNavigator
import pages.sections.dispatch.{DispatchAddressPage, DispatchBusinessNamePage, DispatchUseConsignorDetailsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.dispatch.DispatchUseConsignorDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class DispatchUseConsignorDetailsController @Inject()(override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                      override val betaAllowList: BetaAllowListAction,
                                                      override val navigator: DispatchNavigator,
                                                      override val auth: AuthAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                      formProvider: DispatchUseConsignorDetailsFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: DispatchUseConsignorDetailsView
                                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      renderView(Ok, fillForm(DispatchUseConsignorDetailsPage, formProvider()), mode)
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        cleanseSaveAndRedirect
      )
    }

  private def renderView(status: Status, form: Form[Boolean], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(form, routes.DispatchUseConsignorDetailsController.onSubmit(request.ern, request.draftId, mode))))

  private def cleanseSaveAndRedirect(value: Boolean)(implicit request: DataRequest[_]): Future[Result] = {
    val cleansedAnswers = cleanseUserAnswersIfValueHasChanged(DispatchUseConsignorDetailsPage, value, {
      request.userAnswers
        .remove(DispatchBusinessNamePage)
        .remove(DispatchAddressPage)
    })
    saveAndRedirect(DispatchUseConsignorDetailsPage, value, cleansedAnswers, NormalMode)
  }

}
