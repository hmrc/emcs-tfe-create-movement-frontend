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

package controllers.sections.dispatch


import controllers.AddressControllerBase
import controllers.actions._
import forms.AddressFormProvider
import models.requests.DataRequest
import models.{Mode, UserAddress}
import navigation.DispatchNavigator
import pages.QuestionPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchUseConsignorDetailsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.UserAnswersService
import views.html.AddressView

import javax.inject.Inject

class DispatchAddressController @Inject()(override val messagesApi: MessagesApi,
                                          override val userAnswersService: UserAnswersService,
                                          override val navigator: DispatchNavigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          override val formProvider: AddressFormProvider,
                                          override val controllerComponents: MessagesControllerComponents,
                                          override val view: AddressView
                                         ) extends AddressControllerBase {

  override val addressPage: QuestionPage[UserAddress] = DispatchAddressPage

  override def isConsignorPageOrUsingConsignorDetails(implicit request: DataRequest[_]): Boolean = DispatchUseConsignorDetailsPage.value.contains(true)

  override def onwardCall(mode: Mode)(implicit request: DataRequest[_]): Call =
    controllers.sections.dispatch.routes.DispatchAddressController.onSubmit(request.ern, request.draftId, mode)

  override def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      val prePopPage = (DispatchAddressPage.value, DispatchUseConsignorDetailsPage.value) match {
        case (None, Some(true)) => ConsignorAddressPage
        case _ => DispatchAddressPage
      }
      renderView(Ok, fillForm(prePopPage, formProvider(addressPage, isConsignorPageOrUsingConsignorDetails)), mode)
    }

  override def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result = {
    val isMandatory = DispatchUseConsignorDetailsPage.value.contains(true) || request.isCertifiedConsignor
    status(view(
      form = form,
      addressPage = addressPage,
      onSubmit = onwardCall(mode),
      isConsignorPageOrUsingConsignorDetails = isConsignorPageOrUsingConsignorDetails,
      onSkip = Option.when(!isMandatory)(routes.DispatchAddressController.onSkip(request.ern, request.draftId, mode)),
      headingKey = Some(if (!isMandatory) "dispatchAddress.optional" else "dispatchAddress")
    ))
  }

  def onSkip(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      val updatedUserAnswers = request.userAnswers.remove(DispatchAddressPage)
      saveAndRedirect(addressPage, updatedUserAnswers, mode)
    }
}
