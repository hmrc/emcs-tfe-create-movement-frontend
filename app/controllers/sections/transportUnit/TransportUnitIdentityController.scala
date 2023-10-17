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

package controllers.sections.transportUnit

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.transportUnit.TransportUnitIdentityFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode, TransportUnitType}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitIdentityView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitIdentityController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: TransportUnitNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val userAllowList: UserAllowListAction,
                                                 formProvider: TransportUnitIdentityFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: TransportUnitIdentityView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withTransportUnitType { transportUnitType =>
        Future.successful(Ok(view(fillForm(TransportUnitIdentityPage, formProvider(transportUnitType)), transportUnitType, mode)))
      }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withTransportUnitType { transportUnitType =>
        formProvider(transportUnitType).bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, transportUnitType, mode))),
          value =>
            saveAndRedirect(TransportUnitIdentityPage, value, mode)
        )
      }
    }

  private def withTransportUnitType(f: TransportUnitType => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.get(TransportUnitTypePage) match {
      case Some(value) => f(value)
      case None => logger.warn(s"[withTransportUnitType] No answer, redirecting to get the answer")
        Future.successful(
          Redirect(
            controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(request.ern, request.lrn, NormalMode)
          )
        )
    }
  }
}
