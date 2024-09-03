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

package controllers.sections.destination


import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.destination.DestinationAddressFormProvider
import models.requests.DataRequest
import models.{Mode, UserAddress}
import navigation.DestinationNavigator
import pages.QuestionPage
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.destination.{DestinationAddressPage, DestinationConsigneeDetailsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.UserAnswersService
import views.html.sections.destination.DestinationAddressView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationAddressController @Inject()(override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: DestinationNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                                   val formProvider: DestinationAddressFormProvider,
                                             override val controllerComponents: MessagesControllerComponents,
                                             val view: DestinationAddressView
                                            ) extends BaseNavigationController with AuthActionHelper {

  val addressPage: QuestionPage[UserAddress] = DestinationAddressPage

  def onwardCall(mode: Mode)(implicit request: DataRequest[_]): Call =
    controllers.sections.destination.routes.DestinationAddressController.onSubmit(request.ern, request.draftId, mode)

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      val prePopPage: QuestionPage[UserAddress] = (addressPage.value, DestinationConsigneeDetailsPage.value) match {
        case (None, Some(true)) => ConsigneeAddressPage
        case _ => addressPage
      }
      renderView(Ok, fillForm(prePopPage, formProvider()(request)), mode)
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future.successful(renderView(BadRequest, formWithErrors, mode)),
        saveAndRedirect(addressPage, _, mode)
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result = {
    status(view(
      form = form,
      onSubmit = onwardCall(mode)
    ))

  }
}
