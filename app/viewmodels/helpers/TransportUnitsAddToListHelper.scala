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
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import models.{Index, NormalMode}
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitSection, TransportUnitTypePage}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.TransportUnitsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.transportUnit._
import viewmodels.govuk.summarylist._
import viewmodels.taskList.{Completed, InProgress, TaskListStatus}
import views.html.components.{link, span}

import javax.inject.Inject

class TransportUnitsAddToListHelper @Inject()(implicit link: link, tagHelper: TagHelper, span: span) {

  def allTransportUnitsSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = {
    request.userAnswers.getCount(TransportUnitsCount) match {
      case Some(value) => (0 until value).map(int => summaryList(Index(int)))
      case None => Nil
    }
  }

  private def finalCyaChangeLink()(implicit request: DataRequest[_]): Option[Call] =
    Option.when(
      !(request.userAnswers.getCount(TransportUnitsCount).contains(1) &&
        request.userAnswers.get(TransportUnitTypePage(0)).contains(FixedTransport) &&
        request.userAnswers.get(HowMovementTransportedPage).contains(FixedTransportInstallations))
    )(controllers.sections.transportUnit.routes.TransportUnitsAddToListController.onPageLoad(request.ern, request.draftId))

  def finalCyaSummary()(implicit request: DataRequest[_], messages: Messages): Option[SummaryList] =
    request.userAnswers.getCount(TransportUnitsCount).map { count =>
      SummaryListViewModel(
        rows = (0 until count).flatMap { idx =>
          for {
            transportType <- TransportUnitTypePage(idx).value
          } yield {
            val transportId = TransportUnitIdentityPage(idx).value
            SummaryListRow(
              key = Key(Text(messages("checkYourAnswers.transportUnits.key", idx + 1))),
              value = ValueViewModel(Text(
                s"${messages(s"transportUnitType.$transportType")}${transportId.fold("")(id => s" ($id)")}"
              ))
            )
          }
        }
      ).withCard(
        CardViewModel(
          title = messages("checkYourAnswers.transportUnits.cardTitle"),
          headingLevel = 2,
          actions = finalCyaChangeLink.map(route =>
            Actions(items = Seq(
              ActionItemViewModel(
                href = route.url,
                content = Text(messages("site.change")),
                id = "changeTransportUnits"
              )
            ))
          )
      ))
    }

  private def summaryList(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    val isTransportUnitAFixedTransportInstallation = TransportUnitTypePage(idx).value.contains(FixedTransport)

    val transportUnitSectionStatus = TransportUnitSection(idx).status
    val sectionComplete = transportUnitSectionStatus == Completed

    val showRemoveChangeLink =
      //Hide remove and change link if FTI and this TU is FT and the index is 0
      !(HowMovementTransportedPage.value.contains(FixedTransportInstallations) &&
        isTransportUnitAFixedTransportInstallation &&
        idx == Index(0)
      )

    SummaryListViewModel(
      rows = Seq(
        Some(TransportUnitTypeSummary.row(idx, sectionComplete, showRemoveChangeLink)),
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
              Option.when(showRemoveChangeLink)(removeLink(idx))
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
    )
  }

  private def continueEditingLink(idx: Index, transportUnitSectionStatus: TaskListStatus)
                                 (implicit request: DataRequest[_], messages: Messages): Option[ActionItem] = {
    if (transportUnitSectionStatus == InProgress) {
      Some(
        ActionItemViewModel(
          content = Text(messages("site.continueEditing")),
          href = transportUnitRoutes.TransportUnitTypeController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, NormalMode).url,
          id = s"editTransportUnit${idx.displayIndex}"
        )
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
            tagHelper.incompleteTag()
          )
        ))
      )
    }
  }

}
