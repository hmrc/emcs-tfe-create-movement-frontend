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

package controllers

import controllers.actions._
import forms.AddressFormProvider
import models.requests.DataRequest
import models.{Mode, UserAddress}
import pages.QuestionPage
import play.api.data.Form
import play.api.mvc._
import views.html.AddressView

import scala.concurrent.Future

trait AddressControllerBase extends BaseNavigationController with AuthActionHelper {

  val formProvider: AddressFormProvider
  val controllerComponents: MessagesControllerComponents
  val view: AddressView
  val addressPage: QuestionPage[UserAddress]

  def isConsignorPageOrUsingConsignorDetails(implicit request: DataRequest[_]): Boolean

  def onwardCall(mode: Mode)(implicit request: DataRequest[_]): Call

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      renderView(Ok, fillForm(addressPage, formProvider(addressPage, isConsignorPageOrUsingConsignorDetails)), mode)
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider(addressPage, isConsignorPageOrUsingConsignorDetails).bindFromRequest().fold(
        formWithErrors => Future.successful(renderView(BadRequest, formWithErrors, mode)),
        saveAndRedirect(addressPage, _, mode)
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      form = form,
      addressPage = addressPage,
      isConsignorPageOrUsingConsignorDetails = isConsignorPageOrUsingConsignorDetails,
      onSubmit = onwardCall(mode)
    ))
}
