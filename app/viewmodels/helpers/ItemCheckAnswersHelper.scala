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

import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import models.{CheckMode, GoodsType, Index}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.items._
import viewmodels.govuk.summarylist._

import javax.inject.{Inject, Singleton}

@Singleton
class ItemCheckAnswersHelper @Inject()(
                                        itemExciseProductCodeSummary: ItemExciseProductCodeSummary,
                                        itemCommodityCodeSummary: ItemCommodityCodeSummary,
                                        itemWineOperationsChoiceSummary: ItemWineOperationsChoiceSummary,
                                        itemWineMoreInformationSummary: ItemWineMoreInformationSummary,
                                        itemBulkPackagingSealTypeSummary: ItemBulkPackagingSealTypeSummary,
                                        itemQuantitySummary: ItemQuantitySummary,
                                        itemDegreesPlatoSummary: ItemDegreesPlatoSummary,
                                        itemDesignationOfOriginSummary: ItemDesignationOfOriginSummary,
                                        itemSmallIndependentProducerSummary: ItemSmallIndependentProducerSummary,
                                        itemCheckAnswersPackagingHelper: ItemCheckAnswersPackagingHelper
                                      ) {

  private val headingLevel = 2

  def constructItemDetailsCard(idx: Index, cnCodeInformation: CnCodeInformation)
                              (implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.itemDetailsCardTitle", idx.displayIndex), headingLevel = headingLevel, actions = None)),
      rows = Seq(
        Some(itemExciseProductCodeSummary.row(idx, cnCodeInformation, CheckMode)),
        itemCommodityCodeSummary.row(idx, cnCodeInformation, CheckMode),
        ItemBrandNameSummary.row(idx),
        ItemCommercialDescriptionSummary.row(idx),
        ItemAlcoholStrengthSummary.row(idx),
        itemDegreesPlatoSummary.row(idx),
        ItemMaturationPeriodAgeSummary.row(idx),
        ItemDensitySummary.row(idx),
        ItemFiscalMarksChoiceSummary.row(idx),
        ItemFiscalMarksSummary.row(idx),
        itemDesignationOfOriginSummary.row(idx),
        itemSmallIndependentProducerSummary.row(idx),
        ItemProducerSizeSummary.row(idx)
      ).flatten
    )
  }

  def constructQuantityCard(idx: Index, cnCodeInformation: CnCodeInformation)
                           (implicit request: DataRequest[_], messages: Messages): SummaryList =
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.quantityCardTitle", idx.displayIndex), headingLevel = headingLevel, actions = None)),
      rows = Seq(
        itemQuantitySummary.row(idx, cnCodeInformation.unitOfMeasure),
        ItemNetMassSummary.row(idx),
        ItemGrossMassSummary.row(idx)
      ).flatten
    )

  def constructWineDetailsCard(idx: Index)
                              (implicit request: DataRequest[_], messages: Messages): SummaryList =
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.wineDetailsCardTitle", idx.displayIndex), headingLevel = headingLevel, actions = None)),
      rows = Seq(
        itemWineOperationsChoiceSummary.row(idx),
        ItemWineProductCategorySummary.row(idx),
        ItemWineGrowingZoneSummary.row(idx),
        ItemWineOriginSummary.row(idx),
        Some(itemWineMoreInformationSummary.row(idx))
      ).flatten
    )

  def constructBulkPackagingCard(idx: Index, cnCodeInformation: CnCodeInformation)(implicit request: DataRequest[_], messages: Messages): SummaryList =
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.bulkPackagingCardTitle", idx.displayIndex), headingLevel = headingLevel, actions = None)),
      rows = bulkPackagingSummaryListRows(idx, cnCodeInformation)
    )

  def individualPackagingCards(idx: Index)(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = itemCheckAnswersPackagingHelper.allPackagesSummary(idx)

  private[helpers] def bulkPackagingSummaryListRows(idx: Index, cnCodeInformation: CnCodeInformation)
                                                   (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] =
    Seq(
      ItemBulkPackagingChoiceSummary.row(idx, GoodsType.apply(cnCodeInformation.exciseProductCode)),
      ItemBulkPackagingSelectSummary.row(idx),
      ItemBulkPackagingSealChoiceSummary.row(idx)
    ).flatten ++ itemBulkPackagingSealTypeSummary.rows(idx)
}
