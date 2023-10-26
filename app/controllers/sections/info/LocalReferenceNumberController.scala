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
import forms.sections.info.LocalReferenceNumberFormProvider
import models.Mode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import navigation.InformationNavigator
import pages.sections.info.LocalReferenceNumberPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.PreDraftService
import views.html.sections.info.LocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.Future

class LocalReferenceNumberController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                val preDraftService: PreDraftService,
                                                val navigator: InformationNavigator,
                                                val auth: AuthAction,
                                                val getPreDraftData: PreDraftDataRetrievalAction,
                                                val requirePreDraftData: PreDraftDataRequiredAction,
                                                val getData: DataRetrievalAction,
                                                val requireData: DataRequiredAction,
                                                val userAllowList: UserAllowListAction,
                                                formProvider: LocalReferenceNumberFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: LocalReferenceNumberView
                                              ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {


  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withGuard {
        case (_, isDeferred) =>
          renderView(Ok, fillForm(LocalReferenceNumberPage, formProvider(isDeferred)), isDeferred, mode)
      }
    }

  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withGuard {
        case (_, isDeferred) =>
          formProvider(isDeferred).bindFromRequest().fold(
            formWithErrors =>
              renderView(BadRequest, formWithErrors, isDeferred, mode),
            value =>
              savePreDraftAndRedirect(LocalReferenceNumberPage, value, mode)
          )
      }
    }

  private def renderView(status: Status, form: Form[_], isDeferred: Boolean, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(isDeferred, form, controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(request.ern, mode))))

  private def withGuard(f: (MovementScenario, Boolean) => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withDestinationTypePageAnswer { destinationTypePageAnswer =>
      withDeferredMovementAnswer { isDeferred =>
        f(destinationTypePageAnswer, isDeferred)
      }
    }
  }

}
