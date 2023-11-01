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

package controllers.sections.sad

import controllers.BaseNavigationController
import controllers.actions._
import models.NorthernIrelandRegisteredConsignor
import navigation.SadNavigator
import pages.sections.sad.SadSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject

class SadIndexController @Inject()(
                                    override val userAnswersService: UserAnswersService,
                                    override val navigator: SadNavigator,
                                    override val auth: AuthAction,
                                    override val getData: DataRetrievalAction,
                                    override val requireData: DataRequiredAction,
                                    override val userAllowList: UserAllowListAction,
                                    val controllerComponents: MessagesControllerComponents
                                  ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      if(request.userTypeFromErn == NorthernIrelandRegisteredConsignor) {
        if (SadSection.isCompleted) {
          // TODO: Update to CAM-SAD02 when built
          Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad())
        } else {
          // TODO: Update to CAM-SAD01 when built
          Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad())
        }
      } else {
        // TODO: Update to tasklist when built
        Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad())
      }
    }

}
