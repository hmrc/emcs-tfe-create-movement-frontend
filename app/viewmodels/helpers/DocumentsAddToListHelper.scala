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

package viewmodels.helpers

import models.{Index, NormalMode}
import models.requests.DataRequest
import play.api.i18n.Messages
import queries.DocumentsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.documents.{DocumentDescriptionSummary, DocumentReferenceSummary, DocumentTypeSummary, ReferenceAvailableSummary}
import controllers.sections.documents.routes
import pages.sections.documents.DocumentSection
import viewmodels.govuk.summarylist._
import viewmodels.taskList.InProgress

import javax.inject.Inject

class DocumentsAddToListHelper @Inject()() {

  def allDocumentsSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = {
    request.userAnswers.get(DocumentsCount) match {
      case Some(value) => (0 until value).map(int => summaryList(Index(int)))
      case None => Nil
    }
  }

  private def summaryList(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      rows = Seq(
        DocumentTypeSummary.row(idx),
        ReferenceAvailableSummary.row(idx),
        DocumentReferenceSummary.row(idx),
        DocumentDescriptionSummary.row(idx)
      ).flatten
    ).copy(card = Some(Card(
      title = Some(CardTitle(Text(messages("documentsAddToList.documentCardTitle", idx.displayIndex)))),
      actions = Some(Actions(items = Seq(
        continueEditingLink(idx),
        Some(removeLink(idx))
      ).flatten))
    )))
  }

  private def removeLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    ActionItemViewModel(
      content = Text(messages("site.remove")),
      href = routes.DocumentsRemoveFromListController.onPageLoad(request.ern, request.draftId, idx).url,
      id = s"removeDocuments-${idx.displayIndex}"
    ).withVisuallyHiddenText(messages("documentsAddToList.documentCardTitle", idx.displayIndex))
  }

  private def continueEditingLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[ActionItem] = {
    DocumentSection(idx).status match {
      case InProgress =>
        Some(ActionItemViewModel(
          content = Text(messages("site.continueEditing")),
          href = routes.DocumentTypeController.onPageLoad(request.ern, request.draftId, idx, NormalMode).url,
          id = s"editDocuments-${idx.displayIndex}"
        ).withVisuallyHiddenText(messages("documentsAddToList.documentCardTitle", idx.displayIndex)))
      case _ => None
    }
  }
}
