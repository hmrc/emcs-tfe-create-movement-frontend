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

package viewmodels.checkAnswers.sections.info

import models.CheckMode
import models.requests.DataRequest
import pages.sections.info.InvoiceDetailsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate

object InformationInvoiceDateSummary {

  def formatDateForUIOutput(date: LocalDate)(implicit messages: Messages): String = {
    val monthMessage = messages(s"date.month.${date.getMonthValue}")
    s"${date.getDayOfMonth} $monthMessage ${date.getYear}"
  }

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(InvoiceDetailsPage).map { invoiceDetailsPage =>

      val value: String = formatDateForUIOutput(invoiceDetailsPage.date)

      SummaryListRowViewModel(
        key = "invoiceDetails.invoice-date.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(request.ern, CheckMode).url,
            id = "changeInvoiceDate")
            .withVisuallyHiddenText(messages("invoiceDetails.invoice-date.change.hidden"))
        )
      )

    }
  }

}
