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
import forms.sections.info.DeferredMovementFormProvider
import models.requests.DataRequest
import models.{CheckMode, Mode, ReviewMode}
import navigation.InformationNavigator
import pages.sections.info.DeferredMovementPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{PreDraftService, UserAnswersService}
import utils.{DispatchDateInFutureValidationError, DispatchDateInPastValidationError}
import views.html.sections.info.DeferredMovementView

import javax.inject.Inject
import scala.concurrent.Future

class DeferredMovementController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            val preDraftService: PreDraftService,
                                            val navigator: InformationNavigator,
                                            val auth: AuthAction,
                                            val getPreDraftData: PreDraftDataRetrievalAction,
                                            val requirePreDraftData: PreDraftDataRequiredAction,
                                            val getData: DataRetrievalAction,
                                            val requireData: DataRequiredAction,
                                            formProvider: DeferredMovementFormProvider,
                                            val userAnswersService: UserAnswersService,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: DeferredMovementView) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      renderView(Ok, fillForm(DeferredMovementPage(), formProvider()), controllers.sections.info.routes.DeferredMovementController.onPreDraftSubmit(request.ern, mode))
    }

  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors, controllers.sections.info.routes.DeferredMovementController.onPreDraftSubmit(request.ern, mode)),
        value =>
          savePreDraftAndRedirect(DeferredMovementPage(), value, mode)
      )
    }

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      renderView(
        status = Ok,
        form = fillForm(DeferredMovementPage(isOnPreDraftFlow = false), formProvider()),
        onSubmitCall = controllers.sections.info.routes.DeferredMovementController.onSubmit(request.ern, draftId)
      )
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      val page = DeferredMovementPage(isOnPreDraftFlow = false)
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors, controllers.sections.info.routes.DeferredMovementController.onSubmit(ern, draftId)),
        value => {
          val cleansedAnswers = cleanseUserAnswersIfValueHasChanged(page, value,
            request.userAnswers.copy(submissionFailures =
            //Remove Validation Error that might exist for Dispatch Date as changing the deferred movement answer invalidates the validation
              request.userAnswers.submissionFailures.filterNot(failure =>
                failure.errorType == DispatchDateInPastValidationError.code ||
                  failure.errorType == DispatchDateInFutureValidationError.code
              )
            )
          )
          val mode = if (page.value.contains(value)) CheckMode else ReviewMode
          saveAndRedirect(DeferredMovementPage(isOnPreDraftFlow = false), value, cleansedAnswers, mode)
        }
      )
    }

  def renderView(status: Status, form: Form[_], onSubmitCall: Call)(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(form, onSubmitCall))
    )
  }
}
