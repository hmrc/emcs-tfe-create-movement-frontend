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

package navigation

import controllers.sections.documents.routes
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.documents._
import play.api.mvc.Call

import javax.inject.Inject

class DocumentsNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DocumentsCertificatesPage =>
      (answers: UserAnswers) => {
        answers.get(DocumentsCertificatesPage) match {
          case Some(false) => controllers.sections.documents.routes.DocumentsCheckAnswersController.onPageLoad(answers.ern, answers.draftId)
          case _ => //TODO redirect to CAM-DOC02 when page built
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

      }
    case ReferenceAvailablePage =>
      referenceAvailableRouting()
    case DocumentDescriptionPage =>
      //TODO update with next page when finished
      (userAnswers: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    case DocumentDescriptionPage =>
      //TODO update with next page when finished
      (userAnswers: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    case DocumentsCheckAnswersPage =>
      _ => //TODO redirect CAM02 when built
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    case _ =>
      (userAnswers: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case DocumentsCertificatesPage =>
      (answers) =>
        answers.get(DocumentsCertificatesPage) match {
          case Some(false) => controllers.sections.documents.routes.DocumentsCheckAnswersController.onPageLoad(answers.ern, answers.draftId)
          case _ => //TODO redirect to CAM-DOC02 or CAM-DOC06 when built
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
    case _ =>
      // TODO: update to Add to List CAM-DOC06 page when built as only one option goes to CYA Page
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }

  private def referenceAvailableRouting(mode: Mode = NormalMode): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(ReferenceAvailablePage) match {
      case Some(true) =>
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      case Some(false) =>
        routes.DocumentDescriptionController.onPageLoad(userAnswers.ern, userAnswers.draftId, mode)
      case _ =>
        controllers.routes.JourneyRecoveryController.onPageLoad()
    }
}
