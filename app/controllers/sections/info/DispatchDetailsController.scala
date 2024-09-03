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

import controllers.BasePreDraftNavigationController
import controllers.actions._
import controllers.actions.predraft.{PreDraftAuthActionHelper, PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import forms.sections.info.DispatchDetailsFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.InformationNavigator
import pages.sections.info.{DeferredMovementPage, DispatchDetailsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.DispatchDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class DispatchDetailsController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           val preDraftService: PreDraftService,
                                           val navigator: InformationNavigator,
                                           val auth: AuthAction,
                                           val getPreDraftData: PreDraftDataRetrievalAction,
                                           val requirePreDraftData: PreDraftDataRequiredAction,
                                           val getData: DataRetrievalAction,
                                           val requireData: DataRequiredAction,
                                           formProvider: DispatchDetailsFormProvider,
                                           val userAnswersService: UserAnswersService,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: DispatchDetailsView) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withDeferredMovementAnswer(isOnPreDraftFlow = true) { isDeferred =>
        renderView(
          status = Ok,
          form = fillForm(DispatchDetailsPage(), formProvider(isDeferred)),
          onSubmitCall = controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, mode),
          mode = mode,
          isDeferred = isDeferred,
          isOnPreDraftFlow = true
        )
      }
    }


  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withDeferredMovementAnswer(isOnPreDraftFlow = true) { isDeferred =>
        formProvider(isDeferred).bindFromRequest().fold(
          formWithErrors =>
            renderView(
              status = BadRequest,
              form = formWithErrors,
              onSubmitCall = controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, mode),
              mode = mode,
              isDeferred = isDeferred,
              isOnPreDraftFlow = true
            ),
          value =>
            savePreDraftAndRedirect(DispatchDetailsPage(), value, mode)
        )
      }
    }

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(page = DeferredMovementPage(false)) { isDeferred =>
        renderView(
          status = Ok,
          form = fillForm(DispatchDetailsPage(isOnPreDraftFlow = false), formProvider(isDeferred)),
          onSubmitCall = controllers.sections.info.routes.DispatchDetailsController.onSubmit(request.ern, draftId, mode),
          mode = mode,
          isDeferred = isDeferred,
          isOnPreDraftFlow = false
        )
      }
    }


  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(page = DeferredMovementPage(false)) { isDeferred =>
        formProvider(isDeferred).bindFromRequest().fold(
          formWithErrors =>
            renderView(
              status = BadRequest,
              form = formWithErrors,
              onSubmitCall = controllers.sections.info.routes.DispatchDetailsController.onSubmit(request.ern, draftId, mode),
              mode = mode,
              isDeferred = isDeferred,
              isOnPreDraftFlow = false
            ),
          value =>
            saveAndRedirect(DispatchDetailsPage(isOnPreDraftFlow = false), value, mode)
        )
      }
    }

  def renderView(status: Status, form: Form[_], onSubmitCall: Call, mode: Mode, isDeferred: Boolean, isOnPreDraftFlow: Boolean)
                (implicit request: DataRequest[_]): Future[Result] = {
      Future.successful(
        status(view(
          form = form,
          deferredMovement = isDeferred,
          onSubmitCall = onSubmitCall,
          skipQuestionCall = navigator.nextPage(DispatchDetailsPage(isOnPreDraftFlow), mode, request.userAnswers)
        ))
      )
    }
}
