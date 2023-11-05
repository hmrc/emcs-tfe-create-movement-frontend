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

import models.Index
import models.requests.DataRequest
import play.api.i18n.Messages
import queries.DocumentsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, Card, CardTitle, SummaryList}
import viewmodels.checkAnswers.sections.documents.{DocumentDescriptionSummary, DocumentsCertificatesSummary}
import viewmodels.govuk.summarylist._

import javax.inject.Inject

class DocumentsAddToListHelper @Inject()(implicit link: views.html.components.link) {

  def allDocumentsSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = {
    Seq(summaryList(Index(0)), summaryList(Index(1)))
//    request.userAnswers.get(DocumentsCount) match {
//      case Some(value) => (0 until value).map(int => summaryList(Index(int)))
//      case None => Nil
//    }
  }

  def summaryList(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      rows = Seq(
        DocumentsCertificatesSummary.row(),
        DocumentDescriptionSummary.row()
      ).flatten
    ).copy(card =  Some(Card(
      title = Some(CardTitle(Text(messages("documentsAddToList.documentCardTitle", idx.displayIndex)))),
      actions = Some(Actions( items = Seq(
        ActionItemViewModel(
          content = Text(messages("site.remove")),
          href = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,//TODO UPDATE THIS
          id = s"removeDocuments${idx.displayIndex}"
        ).withVisuallyHiddenText(messages("documentsAddToList.documentCardTitle", idx.displayIndex))
      ))))
    ))
  }

}
