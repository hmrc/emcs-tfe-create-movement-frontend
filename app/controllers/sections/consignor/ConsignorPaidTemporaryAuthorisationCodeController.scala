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

package controllers.sections.consignor

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.consignor.ConsignorPaidTemporaryAuthorisationCodeFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode, NorthernIrelandTemporaryCertifiedConsignor}
import navigation.ConsignorNavigator
import pages.sections.consignor.ConsignorPaidTemporaryAuthorisationCodePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.consignor.ConsignorPaidTemporaryAuthorisationCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ConsignorPaidTemporaryAuthorisationCodeController @Inject()(
                                                                   override val messagesApi: MessagesApi,
                                                                   override val userAnswersService: UserAnswersService,
                                                                   override val navigator: ConsignorNavigator,
                                                                   override val auth: AuthAction,
                                                                   override val getData: DataRetrievalAction,
                                                                   override val requireData: DataRequiredAction,
                                                                   formProvider: ConsignorPaidTemporaryAuthorisationCodeFormProvider,
                                                                   val controllerComponents: MessagesControllerComponents,
                                                                   view: ConsignorPaidTemporaryAuthorisationCodeView
                                                                 ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      authorisedForController {
        renderView(Ok, fillForm(ConsignorPaidTemporaryAuthorisationCodePage, formProvider()), mode)
      }
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      authorisedForController {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, mode),
          saveAndRedirect(ConsignorPaidTemporaryAuthorisationCodePage, _, mode)
        )
      }
    }

  private[consignor] def authorisedForController(f: Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userTypeFromErn match {
      case NorthernIrelandTemporaryCertifiedConsignor => f
      case _ => Future.successful(
        Redirect(routes.ConsignorAddressController.onPageLoad(request.ern, request.draftId, NormalMode))
      )
    }

  private[consignor] def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(
      status(
        view(form, routes.ConsignorPaidTemporaryAuthorisationCodeController.onSubmit(request.ern, request.draftId, mode))
      )
    )

}
