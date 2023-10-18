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
import forms.sections.transportUnit.TransportUnitGiveMoreInformationChoiceFormProvider
import models.requests.DataRequest
import models.{Mode, TransportUnitType}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitGiveMoreInformationChoicePage, TransportUnitTypePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitGiveMoreInformationChoiceController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: TransportUnitNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: TransportUnitGiveMoreInformationChoiceFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: TransportUnitGiveMoreInformationChoiceView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withAnswer(TransportUnitTypePage) { transportUnitType =>
        renderView(Ok, fillForm(TransportUnitGiveMoreInformationChoicePage, formProvider(transportUnitType)), mode, transportUnitType)
      }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withAnswer(TransportUnitTypePage) { transportUnitType =>
        formProvider(transportUnitType).bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors, mode, transportUnitType),
          value =>
            saveAndRedirect(TransportUnitGiveMoreInformationChoicePage, value, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], mode: Mode, transportUnitType: TransportUnitType)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        mode = mode,
        transportUnitType = transportUnitType
      ))
    )
  }
}
