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

package controllers.sections.exportInformation

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import navigation.ExportInformationNavigator
import pages.sections.exportInformation.ExportInformationSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.taskList.UpdateNeeded

import javax.inject.Inject

class ExportInformationIndexController @Inject()(
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: ExportInformationNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  val controllerComponents: MessagesControllerComponents
                                                ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      if (ExportInformationSection.isCompleted || ExportInformationSection.status == UpdateNeeded) {
        Redirect(controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(ern, draftId))
      } else {
        Redirect(controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(ern, draftId, NormalMode))
      }
    }
}
