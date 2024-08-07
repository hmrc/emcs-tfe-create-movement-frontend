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
import models.sections.documents.DocumentsAddToList
import models.{CheckMode, Index, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.documents._
import play.api.mvc.Call
import queries.DocumentsCount

import javax.inject.Inject

class DocumentsNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DocumentsCertificatesPage => documentsCertificatesRouting()
    case DocumentTypePage(idx) => (userAnswers: UserAnswers) =>
      routes.DocumentReferenceController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, NormalMode)
    case DocumentReferencePage(_) =>
      (userAnswers: UserAnswers) => routes.DocumentsAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    case DocumentsAddToListPage => documentsAddToListRouting()
    case DocumentsCheckAnswersPage =>
      (userAnswers: UserAnswers) => controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    case _ =>
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = _ =>
      (userAnswers: UserAnswers) => routes.DocumentsIndexController.onPageLoad(userAnswers.ern, userAnswers.draftId)

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = _ =>
      (userAnswers: UserAnswers) => controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }

  private def documentsCertificatesRouting(mode: Mode = NormalMode): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DocumentsCertificatesPage) match {
      case Some(false) => routes.DocumentsCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      case _ => userAnswers.getCount(DocumentsCount) match {
        case Some(0) | None => routes.DocumentTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, 0, mode)
        case _ => routes.DocumentsAddToListController.onPageLoad(userAnswers.ern, userAnswers.draftId)
      }
    }

  private def documentsAddToListRouting(mode: Mode = NormalMode): UserAnswers => Call = (userAnswers: UserAnswers) => {
    userAnswers.get(DocumentsAddToListPage) match {
      case Some(DocumentsAddToList.Yes) =>
        val idx: Index = userAnswers.getCount(DocumentsCount).fold(0)(identity)
        routes.DocumentTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId, idx, mode)
      case _ =>
        controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    }
  }
}
