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

package viewmodels.checkAnswers.sections.dispatch

import models.CheckMode
import models.requests.DataRequest
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DispatchBusinessNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow =
    DispatchUseConsignorDetailsPage.value match {
      case Some(true) =>
        ConsignorAddressPage.value match {
          case Some(value) => renderRow(value.businessName.getOrElse(""), withChangeLink = false)
          case None => renderRow(messages("site.notProvided"))
        }
      case _ =>
        DispatchAddressPage.value match {
          case Some(name) => renderRow(name.businessName.getOrElse(""))
          case _ => renderRow(messages("site.notProvided"))
        }
    }

  private def renderRow(value: String, withChangeLink: Boolean = true)(implicit request: DataRequest[_], messages: Messages) =
    SummaryListRowViewModel(
      key = "dispatchBusinessName.checkYourAnswersLabel",
      value = ValueViewModel(HtmlFormat.escape(value).toString),
      actions = if (!withChangeLink) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(request.ern, request.draftId, CheckMode).url,
          id = "changeDispatchBusinessName"
        ).withVisuallyHiddenText(messages("dispatchBusinessName.change.hidden"))
      )
    )
}
