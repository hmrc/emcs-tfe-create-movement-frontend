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

package controllers.sections.firstTransporter

import controllers.AddressControllerBase
import controllers.actions._
import forms.AddressFormProvider
import models.requests.DataRequest
import models.{Mode, UserAddress}
import navigation.FirstTransporterNavigator
import pages.QuestionPage
import pages.sections.firstTransporter.FirstTransporterAddressPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Call, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.AddressView

import javax.inject.Inject

class FirstTransporterAddressController @Inject()(override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: FirstTransporterNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val betaAllowList: BetaAllowListAction,
                                                  override val formProvider: AddressFormProvider,
                                                  override val controllerComponents: MessagesControllerComponents,
                                                  override val view: AddressView
                                                 ) extends AddressControllerBase {

  override val addressPage: QuestionPage[UserAddress] = FirstTransporterAddressPage

  override def onwardCall(mode: Mode)(implicit request: DataRequest[_]): Call =
    controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onSubmit(request.ern, request.draftId, mode)

  override def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result = {
    status(view(
      form = form,
      addressPage = addressPage,
      call = onwardCall(mode),
      headingKey = Some("firstTransporterAddress")
    ))

  }


}
