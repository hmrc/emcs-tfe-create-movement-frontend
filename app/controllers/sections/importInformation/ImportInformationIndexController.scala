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

package controllers.sections.importInformation

import controllers.BaseNavigationController
import controllers.actions._
import navigation.ImportInformationNavigator
import pages.sections.importInformation.ImportInformationSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject
import scala.concurrent.Future

class ImportInformationIndexController @Inject()(
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: ImportInformationNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  val controllerComponents: MessagesControllerComponents
                                                ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      if (ImportInformationSection.isCompleted) {
        Future.successful(
          // TODO: change to CAM-IMP02 when built
          Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad())
        )
      } else {
        Future.successful(
          // TODO: change to CAM-IMP01 when built
          Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad())
        )
      }
    }

}
