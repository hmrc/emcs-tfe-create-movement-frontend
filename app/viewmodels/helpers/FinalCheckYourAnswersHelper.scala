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
import pages.sections.consignee.ConsigneeSection
import pages.sections.destination.DestinationSection
import pages.sections.dispatch.DispatchSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.importInformation.ImportInformationSection
import pages.sections.sad.SadSection
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.html.components.GovukSummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.consignee.ConsigneeCheckAnswersHelper
import viewmodels.checkAnswers.sections.consignor.ConsignorCheckAnswersHelper
import viewmodels.checkAnswers.sections.destination.DestinationCheckAnswersHelper
import viewmodels.checkAnswers.sections.dispatch.DispatchCheckAnswersHelper
import viewmodels.checkAnswers.sections.exportInformation.ExportInformationCheckAnswersHelper
import viewmodels.checkAnswers.sections.firstTransporter.FirstTransporterCheckAnswersHelper
import viewmodels.checkAnswers.sections.guarantor.GuarantorCheckAnswersHelper
import viewmodels.checkAnswers.sections.info.InformationCheckAnswersHelper
import viewmodels.checkAnswers.sections.transportArranger.TransportArrangerCheckAnswersHelper

import javax.inject.Inject

class FinalCheckYourAnswersHelper @Inject()(informationCheckAnswersHelper: InformationCheckAnswersHelper,
                                            consignorCheckAnswersHelper: ConsignorCheckAnswersHelper,
                                            dispatchCheckAnswersHelper: DispatchCheckAnswersHelper,
                                            consigneeCheckAnswersHelper: ConsigneeCheckAnswersHelper,
                                            destinationCheckAnswersHelper: DestinationCheckAnswersHelper,
                                            importHelper: CheckYourAnswersImportHelper,
                                            exportInformationCheckAnswersHelper: ExportInformationCheckAnswersHelper,
                                            guarantorCheckAnswersHelper: GuarantorCheckAnswersHelper,
                                            journeyTypeHelper: CheckYourAnswersJourneyTypeHelper,
                                            transportArrangerCheckAnswersHelper: TransportArrangerCheckAnswersHelper,
                                            transportUnitsAddToListHelper: TransportUnitsAddToListHelper,
                                            firstTransporterCheckAnswersHelper: FirstTransporterCheckAnswersHelper,
                                            sadAddToListHelper: SadAddToListHelper,
                                            documentsAddToListHelper: DocumentsAddToListHelper,
                                            govukSummary: GovukSummaryList) {

  //noinspection ScalaStyle
  def cya(deferredMovement: Boolean, itemsSummary: Option[SummaryList])(implicit request: DataRequest[_], messages: Messages): HtmlFormat.Appendable = {
    HtmlFormat.fill(Seq(

      //Movement information card
      Some(govukSummary(informationCheckAnswersHelper.summaryList(deferredMovement, asCard = true))),

      //Consignor card
      Some(govukSummary(consignorCheckAnswersHelper.summaryList(asCard = true))),

      //Import card
      Option.when(ImportInformationSection.canBeCompletedForTraderAndDestinationType) {
        govukSummary(importHelper.summaryList(asCard = true))
      },

      //Dispatch card
      Option.when(DispatchSection.canBeCompletedForTraderAndDestinationType) {
        govukSummary(dispatchCheckAnswersHelper.summaryList(asCard = true))
      },

      //Consignee card
      Option.when(ConsigneeSection.canBeCompletedForTraderAndDestinationType) {
        govukSummary(consigneeCheckAnswersHelper.summaryList(asCard = true))
      },

      //Place of destination card
      Option.when(DestinationSection.canBeCompletedForTraderAndDestinationType) {
        govukSummary(destinationCheckAnswersHelper.summaryList(asCard = true))
      },

      //Export card
      Option.when(ExportInformationSection.canBeCompletedForTraderAndDestinationType) {
        govukSummary(exportInformationCheckAnswersHelper.summaryList(asCard = true))
      },

      //Guarantor card
      Some(govukSummary(guarantorCheckAnswersHelper.summaryList(asCard = true))),

      //Journey type card
      Some(govukSummary(journeyTypeHelper.summaryList(asCard = true))),

      //Journey type card
      Some(govukSummary(transportArrangerCheckAnswersHelper.summaryList(asCard = true))),

      //Transport Units card
      transportUnitsAddToListHelper.finalCyaSummary().map(govukSummary(_)),

      //First Transporter card
      Some(govukSummary(firstTransporterCheckAnswersHelper.summaryList(asCard = true))),

      //Items card
      itemsSummary.map(govukSummary(_)),

      //SAD card
      Option.when(SadSection.canBeCompletedForTraderAndDestinationType) {
        sadAddToListHelper.finalCyaSummary().map(govukSummary(_))
      }.flatten,

      //Documents card
      Some(govukSummary(documentsAddToListHelper.finalCyaSummary()))

    ).flatten)
  }
}
