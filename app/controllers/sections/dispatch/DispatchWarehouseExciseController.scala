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

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.dispatch.DispatchWarehouseExciseFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.DispatchNavigator
import pages.sections.dispatch.{DispatchAddressPage, DispatchWarehouseExcisePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.dispatch.DispatchWarehouseExciseView

import javax.inject.Inject
import scala.concurrent.Future

class DispatchWarehouseExciseController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: DispatchNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   override val betaAllowList: BetaAllowListAction,
                                                   formProvider: DispatchWarehouseExciseFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: DispatchWarehouseExciseView
                                                 ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      renderView(Ok, fillForm(DispatchWarehouseExcisePage, formProvider()), mode)
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        cleanseSaveAndRedirect(_, mode)
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(form, mode)))

  def cleanseSaveAndRedirect(value: String, mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    val cleansedAnswers = {
      //If the ERN has changed from GB -> XI or XI -> GB then Address MUST be re-captured. Hence, remove it.
      if(DispatchWarehouseExcisePage.value.exists(_.startsWith(value.take(2)))) request.userAnswers else {
        request.userAnswers.remove(DispatchAddressPage)
      }
    }
    saveAndRedirect(DispatchWarehouseExcisePage, value, cleansedAnswers, mode)
  }

}
