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

import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import models.Index
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.sections.transportUnit.{TransportUnitSection, TransportUnitTypePage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import queries.TransportUnitsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, Card, CardTitle, SummaryList}
import viewmodels.checkAnswers.sections.transportUnit._
import viewmodels.govuk.summarylist._
import viewmodels.taskList.Completed
import views.html.components.{link, tag, span}

import javax.inject.Inject

class TransportUnitsAddToListHelper @Inject()(implicit link: link, tag: tag, span: span) {

  def allTransportUnitsSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = {
    request.userAnswers.get(TransportUnitsCount) match {
      case Some(value) => (0 until value).map(int => summaryList(Index(int)))
      case None => Nil
    }
  }

  private def summaryList(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    val isTransportUnitAFixedTransportInstallation = request.userAnswers.get(TransportUnitTypePage(idx)).contains(FixedTransport)

    SummaryListViewModel(
      rows = Seq(
        Some(TransportUnitTypeSummary.row(idx)),
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportUnitIdentitySummary.row(idx)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportSealChoiceSummary.row(idx)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportSealTypeSummary.row(idx)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportSealInformationSummary.row(idx)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportUnitGiveMoreInformationSummary.row(idx)) else None
      ).flatMap(_.flatten)
    ).copy(card = Some(
      Card(
        title = Some(createCardTitle(idx)),
        actions = Some(
          Actions(
            items = Seq(
              ActionItemViewModel(
                content = Text(messages("site.remove")),
                href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx).url,
                id = s"removeTransportUnit${idx.displayIndex}"
              ).withVisuallyHiddenText(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex))
            )
          )
        )
      )
    ))
  }

  private def createCardTitle(idx: Index)(implicit request: DataRequest[_], messages: Messages): CardTitle = {
    if (TransportUnitSection(idx).status == Completed) {
      CardTitle(Text(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex)))
    } else {
      CardTitle(
        HtmlContent(HtmlFormat.fill(
          Seq(
            span(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex), Some("govuk-!-margin-right-2")),
            tag(
              message = messages("taskListStatus.incomplete"),
              colour = "red"
            )
          )
        ))
      )
    }
  }

}
