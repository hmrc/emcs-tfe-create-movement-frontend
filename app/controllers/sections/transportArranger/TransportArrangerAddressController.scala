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

package controllers.sections.transportArranger

import controllers.AddressControllerBase
import controllers.actions._
import forms.AddressFormProvider
import models.requests.DataRequest
import models.{Mode, UserAddress}
import navigation.TransportArrangerNavigator
import pages.QuestionPage
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Call, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.AddressView

import javax.inject.Inject

class TransportArrangerAddressController @Inject()(override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: TransportArrangerNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                               override val formProvider: AddressFormProvider,
                                                   override val controllerComponents: MessagesControllerComponents,
                                                   override val view: AddressView
                                                  ) extends AddressControllerBase {

  override val addressPage: QuestionPage[UserAddress] = TransportArrangerAddressPage

  // when user selects Consignor for Guarantor section, they should not ever hit this page
  override def isConsignorPageOrUsingConsignorDetails(implicit request: DataRequest[_]): Boolean = false

  override def onwardCall(mode: Mode)(implicit request: DataRequest[_]): Call =
    controllers.sections.transportArranger.routes.TransportArrangerAddressController.onSubmit(request.ern, request.draftId, mode)

  override def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result = {
    withAnswer(
      page = TransportArrangerPage,
      redirectRoute = controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.draftId)
    ) { arranger =>
      status(view(
        form = form,
        addressPage = addressPage,
        onSubmit = onwardCall(mode),
        isConsignorPageOrUsingConsignorDetails = isConsignorPageOrUsingConsignorDetails,
        headingKey = Some(s"$TransportArrangerAddressPage.$arranger")
      ))
    }
  }


}
