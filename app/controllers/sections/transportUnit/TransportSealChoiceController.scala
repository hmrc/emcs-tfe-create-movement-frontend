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
import models.{Mode, NormalMode}
import navigation.TransportUnitNavigator
import pages.{TransportSealChoicePage, TransportUnitTypePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
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
    authorisedDataRequest(ern, lrn) { implicit request =>
      request.userAnswers.get(TransportUnitTypePage) match {
        case Some(answer) =>
          Ok(view(fillForm(TransportSealChoicePage, formProvider(answer)), mode, answer))
        case _ =>
          Redirect(controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(ern,lrn,NormalMode))

      }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      val transportUnitType = request.userAnswers.get(TransportUnitTypePage).get
      formProvider(transportUnitType).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, transportUnitType))),
        value =>
          saveAndRedirect(TransportSealChoicePage, value, mode)
      )
    }
}
