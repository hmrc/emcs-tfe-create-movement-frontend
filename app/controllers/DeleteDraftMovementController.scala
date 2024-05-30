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
import config.Constants.TFE_DELETED_DRAFT_LRN
import controllers.actions._
import forms.DeleteDraftMovementFormProvider
import handlers.ErrorHandler
import models.requests.DataRequest
import navigation.Navigator
import pages.sections.info.LocalReferenceNumberPage
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
                                       appConfig: AppConfig,
                                       errorHandler: ErrorHandler
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
          request.userAnswers.get(LocalReferenceNumberPage(isOnPreDraftFlow = false)).fold({
            logger.error("Error trying to delete draft, LRN is missing - rendering ISE")
            Future(InternalServerError(errorHandler.internalServerErrorTemplate))
          })(
            lrn => service.deleteDraft().map(_ => Redirect(appConfig.emcsTfeDraftsUrl(ern)).flashing(TFE_DELETED_DRAFT_LRN -> lrn))
          )
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
