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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.PreDraftService
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
                                           val controllerComponents: MessagesControllerComponents,
                                           view: DispatchDetailsView,
                                           val userAllowList: UserAllowListAction
                                         ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      renderView(Ok, fillForm(DispatchDetailsPage, formProvider()), mode)
    }


  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors, mode),
        value =>
          savePreDraftAndRedirect(DispatchDetailsPage, value, mode)
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = DeferredMovementPage,
      redirectRoute = controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(request.ern, mode)
    ) { deferredMovement =>
      Future.successful(
        status(view(
          form = form,
          deferredMovement = deferredMovement,
          onSubmitCall = controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, mode),
          skipQuestionCall = navigator.nextPage(DispatchDetailsPage, mode, request.userAnswers)
        ))
      )
    }
  }


}
