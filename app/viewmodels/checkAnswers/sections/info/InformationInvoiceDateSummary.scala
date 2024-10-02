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

import com.google.inject.Inject
import models.CheckMode
import models.requests.DataRequest
import models.sections.info.InvoiceDetailsModel
import pages.sections.info.InvoiceDetailsPage
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import utils.DateTimeUtils
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import viewmodels.implicits._
import views.html.components

class InformationInvoiceDateSummary @Inject()(link: components.link,
                                              tagHelper: TagHelper) extends DateTimeUtils {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    val data: Option[InvoiceDetailsModel] = InvoiceDetailsPage().value

    val changeLink = if (isOnPreDraftFlow) {
      controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(request.ern, CheckMode)
    } else {
      controllers.sections.info.routes.InvoiceDetailsController.onPageLoad(request.ern, request.draftId, CheckMode)
    }

    val value: Html = data match {
      case Some(invoiceDetailsPage) => HtmlFormat.escape(invoiceDetailsPage.date.formatDateForUIOutput())
      case None => link(
        link = changeLink.url,
        messageKey = messages("invoiceDetails.invoice-date.add"),
        id = Some("changeInvoiceDate")
      )
    }

    Some(
      SummaryListRowViewModel(
        key = "invoiceDetails.invoice-date.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(value)),
        actions =
          (data, isOnPreDraftFlow) match {
            case (None, true) =>
              Seq()
            case (None, false) =>
              Seq(ActionItem(
                content = HtmlContent(tagHelper.incompleteTag(withNoFloat = true)),
                href = changeLink.url,
                visuallyHiddenText = Some(messages("invoiceDetails.invoice-date.add")),
                classes = "cursor-default",
                attributes = Map("tabindex" -> "-1")
              ))
            case (Some(_), preDraft) =>
              Seq(ActionItemViewModel(
                content = "site.change",
                href = changeLink.url,
                id = "changeInvoiceDate"
              ).withVisuallyHiddenText(messages("invoiceDetails.invoice-date.change.hidden")))
          }
      )
    )

  }

}
