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
import navigation.DispatchNavigator
import pages.sections.dispatch.DispatchSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject
import scala.concurrent.Future

class DispatchIndexController @Inject()(
                                         override val userAnswersService: UserAnswersService,
                                         override val navigator: DispatchNavigator,
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val userAllowList: UserAllowListAction,
                                         val controllerComponents: MessagesControllerComponents,
                                       ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      if (DispatchSection.isCompleted) {
        Future.successful(Redirect(controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(ern, draftId)))
      } else {
        // TODO: Change redirect location when CAM-DIS01 is built
        Future.successful(Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad()))
      }
    }
}
