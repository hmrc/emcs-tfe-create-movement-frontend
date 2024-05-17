/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers

import config.AppConfig
import controllers.actions._
import forms.DeleteDraftMovementFormProvider
import models.requests.DataRequest
import navigation.Navigator
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{DeleteDraftMovementService, UserAnswersService}
import views.html.DeleteDraftMovementView

import javax.inject.Inject
import scala.concurrent.Future

class DeleteDraftMovementController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val betaAllowList: BetaAllowListAction,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: DeleteDraftMovementFormProvider,
                                       service: DeleteDraftMovementService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeleteDraftMovementView,
                                       appConfig: AppConfig
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      renderView(Ok, formProvider())
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        deleteDraft => if(deleteDraft) {
          service.deleteDraft().map(_ => Redirect(appConfig.emcsTfeHomeUrl))
        } else {
          Future(Redirect(controllers.routes.DraftMovementController.onPageLoad(ern, draftId)))
        }

      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form,
      action = controllers.routes.DeleteDraftMovementController.onSubmit(request.ern, request.draftId)
    )))
}
