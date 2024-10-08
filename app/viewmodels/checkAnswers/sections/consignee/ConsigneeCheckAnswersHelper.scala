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

package viewmodels.checkAnswers.sections.consignee

import models.requests.DataRequest
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import views.html.components.list

import javax.inject.Inject

class ConsigneeCheckAnswersHelper @Inject()(list: list, consigneeExciseSummary: ConsigneeExciseSummary) {

  def summaryList(consigneeReviewVatEori: Boolean = false,
                  consigneeReviewBusinessName: Boolean = false,
                  asCard: Boolean = false)
                 (implicit request: DataRequest[_], messages: Messages): SummaryList = {

    val summary = if(consigneeReviewVatEori) {
      SummaryListViewModel(
        rows = Seq(
          ConsigneeExportInformationSummary(list).row(),
          ConsigneeExportVatSummary.row(showActionLinks = true),
          ConsigneeExportEoriSummary.row(showActionLinks = true),
        ).flatten
      )
    }
    else if (consigneeReviewBusinessName) {
      SummaryListViewModel(
        rows = Seq(
          ConsigneeAddressSummary.row(showActionLinks = true),
          consigneeExciseSummary.row(showActionLinks = true),
          ConsigneeExemptOrganisationSummary.row(showActionLinks = true)
        ).flatten
      )
    } else {
      SummaryListViewModel(
        rows = Seq(
          ConsigneeExportInformationSummary(list).row(),
          consigneeExciseSummary.row(showActionLinks = true),
          ConsigneeExportVatSummary.row(showActionLinks = true),
          ConsigneeExportEoriSummary.row(showActionLinks = true),
          ConsigneeExemptOrganisationSummary.row(showActionLinks = true),
          ConsigneeAddressSummary.row(showActionLinks = true)
        ).flatten
      )
    }

    if(asCard) {
      summary.withCard(CardViewModel(messages("checkYourAnswers.consignee.cardTitle"), 2, None))
    } else {
      summary
    }
  }

}
