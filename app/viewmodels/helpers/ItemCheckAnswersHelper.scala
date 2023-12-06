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
import models.{CheckMode, Index}
import pages.sections.items.ItemWineMoreInformationPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.sections.items._

import javax.inject.Inject

class ItemCheckAnswersHelper @Inject()(
                                        itemExciseProductCodeSummary: ItemExciseProductCodeSummary,
                                        itemCommodityCodeSummary: ItemCommodityCodeSummary,
                                        itemWineOperationsChoiceSummary: ItemWineOperationsChoiceSummary,
                                        itemWineMoreInformationSummary: ItemWineMoreInformationSummary
                                      ) {

  def constructItemDetailsCard(idx: Index, cnCodeInformation: CnCodeInformation)
                              (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] =
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

  def constructQuantityCard(idx: Index, cnCodeInformation: CnCodeInformation)
                           (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] =
    Seq(
      ItemQuantitySummary.row(idx, cnCodeInformation.unitOfMeasure),
      ItemNetMassSummary.row(idx),
      ItemGrossMassSummary.row(idx),
    ).flatten

  def constructWineDetailsCard(idx: Index)
                           (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] =
    Seq(
      itemWineOperationsChoiceSummary.row(idx),
      ItemImportedWineChoiceSummary.row(idx),
      ItemWineGrowingZoneSummary.row(idx),
      ItemWineOriginSummary.row(idx),
      Some(itemWineMoreInformationSummary.row(idx))
    ).flatten
}
