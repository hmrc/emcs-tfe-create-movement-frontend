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

import controllers.sections.documents.routes
import models.requests.DataRequest
import models.{CheckMode, Index, NormalMode}
import pages.sections.documents.{DocumentSection, DocumentTypePage}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.DocumentsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.{ActionItem, SummaryListRow, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, HtmlContent}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.documents._
import viewmodels.govuk.TagFluency
import viewmodels.govuk.summarylist._
import viewmodels.taskList.InProgress

import javax.inject.Inject

class DocumentsAddToListHelper @Inject()(tagHelper: TagHelper, span: views.html.components.span) extends TagFluency {


  def allDocumentsSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = {
    request.userAnswers.getCount(DocumentsCount) match {
      case Some(value) => (0 until value).map(int => summaryList(Index(int)))
      case None => Nil
    }
  }

  def finalCyaSummary()(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    def withCardChangeLink(url: Call): SummaryList => SummaryList =
      _.withCard(CardViewModel(messages("checkYourAnswers.documents.cardTitle"), 2, Some(
        Actions(items = Seq(
          ActionItemViewModel(
            href = url.url,
            content = Text(messages("site.change")),
            id = "changeDocuments"
          )
        ))
      )))

    request.userAnswers.getCount(DocumentsCount) match {
      case Some(count) if count > 0 =>
        withCardChangeLink(controllers.sections.documents.routes.DocumentsAddToListController.onPageLoad(request.ern, request.draftId)) {
          SummaryListViewModel(
            rows = (0 until count).flatMap { idx =>
              DocumentTypePage(idx).value.map { documentType =>
                SummaryListRow(
                  key = Key(Text(messages("checkYourAnswers.documents.key", idx + 1))),
                  value = ValueViewModel(Text(documentType.description))
                )
              }
            }
          )
        }
      case _ =>
        withCardChangeLink(controllers.sections.documents.routes.DocumentsCertificatesController.onPageLoad(request.ern, request.draftId, CheckMode)) {
          SummaryListViewModel(
            rows = Seq(SummaryListRow(
              key = Key(Text(messages("checkYourAnswers.documents.noDocuments"))),
              value = ValueViewModel(Empty)
            ))
          )
        }
    }
  }

  private def summaryList(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      rows = Seq(
        DocumentTypeSummary.row(idx),
        DocumentReferenceSummary.row(idx)
      ).flatten
    ).withCard(
      Card(
        title = Some(cardTitle(idx)),
        actions = Some(Actions(items = Seq(
          continueEditingLink(idx),
          Some(removeLink(idx))
        ).flatten))
      )
    )
  }

  private def cardTitle(idx: Index)(implicit request: DataRequest[_], messages: Messages): CardTitle = {

    DocumentSection(idx).status match {
      case InProgress => CardTitle(HtmlContent(HtmlFormat.fill(Seq(
        span(messages("documentsAddToList.documentCardTitle", idx.displayIndex), Some("govuk-!-margin-right-2")),
        tagHelper.incompleteTag()
      ))))
      case _ => CardTitle(HtmlContent(span(messages("documentsAddToList.documentCardTitle", idx.displayIndex))))
    }
  }

  private def removeLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    ActionItemViewModel(
      content = Text(messages("site.remove")),
      href = routes.DocumentsRemoveFromListController.onPageLoad(request.ern, request.draftId, idx).url,
      id = s"removeDocuments-${idx.displayIndex}"
    )
  }

  private def continueEditingLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[ActionItem] = {
    DocumentSection(idx).status match {
      case InProgress =>
        Some(ActionItemViewModel(
          content = Text(messages("site.continueEditing")),
          href = routes.DocumentTypeController.onPageLoad(request.ern, request.draftId, idx, NormalMode).url,
          id = s"editDocuments-${idx.displayIndex}"
        ))
      case _ => None
    }
  }
}
