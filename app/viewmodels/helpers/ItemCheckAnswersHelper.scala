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

import controllers.sections.items.routes
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import models.{CheckMode, Index, UnitOfMeasure}
import pages.sections.items._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, SummaryListRow, Value}
import viewmodels.checkAnswers.sections.items._
import viewmodels.implicits._
import views.html.components.p

import javax.inject.Inject

class ItemCheckAnswersHelper @Inject()(
                                        itemExciseProductCodeSummary: ItemExciseProductCodeSummary,
                                        itemCommodityCodeSummary: ItemCommodityCodeSummary,
                                        p: p
                                      ) {

  private def summaryListRowBuilder(key: Content, value: Content, changeLink: Option[ActionItem]) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row",
    actions = changeLink.map(actionItem => Actions(items = Seq(actionItem)))
  )

  object ItemDetails {
    def constructCard(idx: Index, cnCodeInformation: CnCodeInformation)
                     (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
      Seq(
        Some(itemExciseProductCodeSummary.row(idx, cnCodeInformation, CheckMode)),
        itemCommodityCodeSummary.row(idx, cnCodeInformation, CheckMode),
        ItemBrandNameSummary.row(idx),
        ItemCommercialDescriptionSummary.row(idx),
        ItemAlcoholStrengthSummary.row(idx),
        ItemDegreesPlatoSummary.row(idx),
        ItemMaturationPeriodAgeSummary.row(idx),
        ItemDensitySummary.row(idx),
        ItemFiscalMarksChoiceSummary.row(idx),
        ItemFiscalMarksSummary.row(idx),
        ItemGeographicalIndicationChoiceSummary.row(idx),
        ItemGeographicalIndicationSummary.row(idx),
        ItemSmallIndependentProducerSummary.row(idx),
        ItemProducerSizeSummary.row(idx),
      ).flatten
    }
  }

  object Quantity {
    def constructCard(idx: Index, cnCodeInformation: CnCodeInformation)
                     (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
      Seq(
        constructQuantityRow(idx, cnCodeInformation.unitOfMeasure),
        constructNetMassRow(idx, cnCodeInformation.unitOfMeasure),
        constructGrossMassRow(idx, cnCodeInformation.unitOfMeasure),
      ).flatten
    }

    private[helpers] def constructQuantityRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                             (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemQuantityPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.quantity.value", answer, unitOfMeasure.toShortFormatMessage()),
            changeLink = Some(ActionItem(
              href = routes.ItemQuantityController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "site.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructNetMassRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                            (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemNetGrossMassPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.netMass.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.netMass.value", answer.netMass, unitOfMeasure.toShortFormatMessage()),
            changeLink = Some(ActionItem(
              href = routes.ItemNetGrossMassController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "site.change",
              visuallyHiddenText = Some(messages(s"$page.netMass.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructGrossMassRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                              (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemNetGrossMassPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.grossMass.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.grossMass.value", answer.grossMass, unitOfMeasure.toShortFormatMessage()),
            changeLink = Some(ActionItem(
              href = routes.ItemNetGrossMassController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "site.change",
              visuallyHiddenText = Some(messages(s"$page.grossMass.change.hidden"))
            ))
          )
      }
    }
  }
}
