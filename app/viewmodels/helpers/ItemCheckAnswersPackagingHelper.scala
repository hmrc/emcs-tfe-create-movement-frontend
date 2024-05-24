/*
 * Copyright 2024 HM Revenue & Customs
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

import controllers.sections.items.routes
import models.requests.DataRequest
import models.{Index, NormalMode}
import pages.sections.items.ItemsPackagingSectionItems
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import queries.ItemsPackagingCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.items._
import viewmodels.govuk.TagFluency
import viewmodels.govuk.summarylist._
import viewmodels.taskList.InProgress

import javax.inject.{Inject, Singleton}

@Singleton
class ItemCheckAnswersPackagingHelper @Inject()(tagHelper: TagHelper,
                                                span: views.html.components.span,
                                                itemPackagingSealInformationSummary: ItemPackagingSealInformationSummary) extends TagFluency {


  def allPackagesSummary(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] =
    request.userAnswers.get(ItemsPackagingCount(itemIdx)) match {
      case Some(value) => (0 until value).map(packagingIdx => summaryList(itemIdx, Index(packagingIdx)))
      case None => Nil
    }

  private def summaryList(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      rows = Seq(
        ItemSelectPackagingSummary.row(itemIdx, packagingIdx),
        ItemPackagingQuantitySummary.row(itemIdx, packagingIdx),
        ItemPackagingShippingMarksChoiceSummary.row(itemIdx, packagingIdx),
        ItemPackagingShippingMarksSummary.row(itemIdx, packagingIdx),
        ItemPackagingSealChoiceSummary.row(itemIdx, packagingIdx),
        ItemPackagingSealTypeSummary.row(itemIdx, packagingIdx),
        itemPackagingSealInformationSummary.row(itemIdx, packagingIdx)
      ).flatten
    ).copy(card = Some(Card(
      title = Some(cardTitle(itemIdx, packagingIdx)),
      actions = Some(Actions(items = Seq(
        continueEditingLink(itemIdx, packagingIdx),
        Some(removeLink(itemIdx, packagingIdx))
      ).flatten))
    )))
  }

  private def cardTitle(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): CardTitle = {

    ItemsPackagingSectionItems(itemIdx, packagingIdx).status match {
      case InProgress => CardTitle(HtmlContent(HtmlFormat.fill(Seq(
        span(messages("itemCheckAnswers.packagingCardTitle", packagingIdx.displayIndex, itemIdx.displayIndex), Some("govuk-!-margin-right-2")),
        tagHelper.incompleteTag()
      ))))
      case _ => CardTitle(HtmlContent(span(messages("itemCheckAnswers.packagingCardTitle", packagingIdx.displayIndex, itemIdx.displayIndex))))
    }
  }

  private def removeLink(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    ActionItemViewModel(
      content = Text(messages("site.remove")),
      href = routes.ItemPackagingRemovePackageController.onPageLoad(request.ern, request.draftId, itemIdx, packagingIdx).url,
      id = s"removePackage-${packagingIdx.displayIndex}"
    ).withVisuallyHiddenText(messages("itemCheckAnswers.packagingCardTitle", packagingIdx.displayIndex, itemIdx.displayIndex))
  }

  private def continueEditingLink(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[ActionItem] = {
    ItemsPackagingSectionItems(itemIdx, packagingIdx).status match {
      case InProgress =>
        Some(ActionItemViewModel(
          content = Text(messages("site.continueEditing")),
          href = routes.ItemSelectPackagingController.onPageLoad(request.ern, request.draftId, itemIdx, packagingIdx, NormalMode).url,
          id = s"editPackage-${packagingIdx.displayIndex}"
        ).withVisuallyHiddenText(messages("itemCheckAnswers.packagingCardTitle", packagingIdx.displayIndex, itemIdx.displayIndex)))
      case _ => None
    }
  }
}
