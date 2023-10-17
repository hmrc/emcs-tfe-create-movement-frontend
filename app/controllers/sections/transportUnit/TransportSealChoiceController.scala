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
import forms.TransportSealChoiceFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode, TransportUnitType}
import navigation.TransportUnitNavigator
import pages.{TransportSealChoicePage, TransportUnitTypePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportUnit.TransportSealChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class TransportSealChoiceController @Inject()(override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val userAllowList: UserAllowListAction,
                                              override val navigator: TransportUnitNavigator,
                                              override val auth: AuthAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              formProvider: TransportSealChoiceFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: TransportSealChoiceView
                                             ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withAnswer(TransportUnitTypePage) { transportUnitType =>
        renderView(Ok, fillForm(TransportSealChoicePage, formProvider(transportUnitType)), transportUnitType, mode)
      }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      val transportUnitType = request.userAnswers.get(TransportUnitTypePage).get
      formProvider(transportUnitType).bindFromRequest().fold(
        formWithError => withAnswer(TransportUnitTypePage) { transportUnitType =>
          renderView(BadRequest, formWithError, transportUnitType, mode)
        },
        saveAndRedirect(TransportSealChoicePage, _, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], transportUnitType: TransportUnitType, mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    Future(status(view(
      form = form,
      mode = mode,
      transportUnitType = transportUnitType,
      onSubmitCall = controllers.sections.transportUnit.routes.TransportSealChoiceController.onSubmit(request.ern, request.lrn, mode)
    )))
  }
}
