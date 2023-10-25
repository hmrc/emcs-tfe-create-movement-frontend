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

package controllers.sections.importInformation

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.importInformation.ImportCustomsOfficeCodeFormProvider
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.{GreatBritainRegisteredConsignor, Mode, NorthernIrelandRegisteredConsignor, UserType}
import navigation.ImportInformationNavigator
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.importInformation.ImportCustomsOfficeCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ImportCustomsOfficeCodeController @Inject()(override val messagesApi: MessagesApi,
                                                  override val auth: AuthAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val navigator: ImportInformationNavigator,
                                                  override val userAnswersService: UserAnswersService,
                                                  formProvider: ImportCustomsOfficeCodeFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: ImportCustomsOfficeCodeView
                                                 ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        Future.successful(Ok(view(
          fillForm(ImportCustomsOfficeCodePage, formProvider()),
          routes.ImportCustomsOfficeCodeController.onSubmit(ern, draftId, mode),
          userType
        )))
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(
              BadRequest(view(
                formWithErrors,
                routes.ImportCustomsOfficeCodeController.onSubmit(ern, draftId, mode),
                userType
              ))
            ),
          customsOfficeCode =>
            saveAndRedirect(ImportCustomsOfficeCodePage, customsOfficeCode, mode)
        )
    }

  private def userType(implicit request: DataRequest[_]): UserType =
    if (request.request.isRegisteredConsignor && request.request.isNorthernIrelandErn) {
      NorthernIrelandRegisteredConsignor
    }
    else if (request.request.isRegisteredConsignor) {
      GreatBritainRegisteredConsignor
    }
    else {
      throw InvalidUserTypeException("[CustomsOfficeController] User must be XIRC or GBRC user type")
    }

}
