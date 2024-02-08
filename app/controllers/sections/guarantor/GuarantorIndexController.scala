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

package controllers.sections.guarantor

import controllers.actions._
import models.NormalMode
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import navigation.GuarantorNavigator
import pages.sections.guarantor.{GuarantorRequiredPage, GuarantorSection}
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorIndexController @Inject()(
                                          override val userAnswersService: UserAnswersService,
                                          override val navigator: GuarantorNavigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          override val betaAllowList: BetaAllowListAction,
                                          val controllerComponents: MessagesControllerComponents
                                        ) extends GuarantorBaseController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      if (GuarantorSection.isCompleted) {
        Future(Redirect(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(ern, draftId)))
      } else {
        if(request.isUkToEuMovement) {
          request.userAnswers.get(HowMovementTransportedPage) match {
            case Some(FixedTransportInstallations) | None =>
              Future(Redirect(controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(ern, draftId, NormalMode)))
            case Some(_) =>
              saveAndRedirect(GuarantorRequiredPage, true, NormalMode)
          }
        } else {
          Future(Redirect(controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(ern, draftId, NormalMode)))
        }
      }
    }
}
