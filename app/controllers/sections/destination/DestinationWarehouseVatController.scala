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

import controllers.actions._
import controllers.BaseNavigationController
import forms.sections.destination.DestinationWarehouseVatFormProvider
import javax.inject.Inject
import models.Mode
import navigation.DestinationNavigator
import pages.DestinationWarehouseVatPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.destination.DestinationWarehouseVatView

import scala.concurrent.Future

class DestinationWarehouseVatController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: DestinationNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: DestinationWarehouseVatFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DestinationWarehouseVatView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, lrn)  { implicit request =>
      // andThen getData
      val destinationType = "TEST destiantion"
//      val destinationType = request.userAnswers.get(DestinationTypePage)
//      withAnswer(DestinationTypePage) {
//        case destinationType.contains()=>
          Ok(view(fillForm(DestinationWarehouseVatPage, formProvider()), mode, destinationType))
//        case _ => value.toEmpty
//          saveAndRedirect(DestinationWarehouseVatPage, value, mode)
      }

//    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
 //     val destinationType = request.userAnswers.get(DestinationTypePage)
      val destinationType = "TEST destiantion"
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, destinationType))),
        value =>
          saveAndRedirect(DestinationWarehouseVatPage, value, mode)
      )
    }
}
