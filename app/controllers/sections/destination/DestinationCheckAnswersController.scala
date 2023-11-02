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
import models.NormalMode
import navigation.DestinationNavigator
import pages.sections.destination.DestinationCheckAnswersPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.destination.DestinationCheckAnswersHelper
import views.html.sections.destination.DestinationCheckAnswersView

import javax.inject.Inject

class DestinationCheckAnswersController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: DestinationNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   val destinationCheckAnswersHelper: DestinationCheckAnswersHelper,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: DestinationCheckAnswersView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      withAnswer(DestinationTypePage) { _ =>
        Ok(view(
          list = destinationCheckAnswersHelper.summaryList(),
          onSubmitCall = controllers.sections.destination.routes.DestinationCheckAnswersController.onSubmit(request.ern, request.draftId)
        ))
      }
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      Redirect(navigator.nextPage(DestinationCheckAnswersPage, NormalMode, request.userAnswers))
    }
}
