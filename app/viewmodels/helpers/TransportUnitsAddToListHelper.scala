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
import models.{Index, NormalMode}
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.sections.transportUnit.{TransportUnitSection, TransportUnitTypePage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import queries.TransportUnitsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Card, CardTitle, SummaryList}
import viewmodels.checkAnswers.sections.transportUnit._
import viewmodels.govuk.summarylist._
import viewmodels.taskList.{Completed, InProgress, TaskListStatus}
import views.html.components.{link, span, tag}

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

    val transportUnitSectionStatus = TransportUnitSection(idx).status
    val sectionComplete = transportUnitSectionStatus == Completed

    SummaryListViewModel(
      rows = Seq(
        Some(TransportUnitTypeSummary.row(idx, sectionComplete)),
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportUnitIdentitySummary.row(idx, sectionComplete)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportSealChoiceSummary.row(idx, sectionComplete)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportSealTypeSummary.row(idx, sectionComplete)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportSealInformationSummary.row(idx, sectionComplete)) else None,
        if (!isTransportUnitAFixedTransportInstallation) Some(TransportUnitGiveMoreInformationSummary.row(idx, sectionComplete)) else None
      ).flatMap(_.flatten)
    ).copy(card = Some(
      Card(
        title = Some(createCardTitle(idx, transportUnitSectionStatus)),
        actions = Some(
          Actions(
            items = Seq(
              continueEditingLink(idx, transportUnitSectionStatus),
              Some(removeLink(idx))
            ).flatten
          )
        )
      )
    ))
  }

  private def removeLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    ActionItemViewModel(
      content = Text(messages("site.remove")),
      href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx).url,
      id = s"removeTransportUnit${idx.displayIndex}"
    ).withVisuallyHiddenText(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex))
  }

  private def continueEditingLink(idx: Index, transportUnitSectionStatus: TaskListStatus)
                                 (implicit request: DataRequest[_], messages: Messages): Option[ActionItem] = {
    if (transportUnitSectionStatus == InProgress) {
      Some(
        ActionItemViewModel(
          content = Text(messages("site.continueEditing")),
          href = transportUnitRoutes.TransportUnitTypeController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, NormalMode).url,
          id = s"editTransportUnit${idx.displayIndex}"
        ).withVisuallyHiddenText(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex))
      )
    } else {
      None
    }
  }

  private def createCardTitle(idx: Index, transportUnitSectionStatus: TaskListStatus)(implicit messages: Messages): CardTitle = {
    if (transportUnitSectionStatus == Completed) {
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
