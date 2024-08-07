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

package viewmodels.checkAnswers.sections.guarantor

import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger._
import models.{CheckMode, NormalMode}
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    val guarantorArrangerAnswer = GuarantorArrangerPage.value
    val guarantorRequiredAnswerIsTrue = GuarantorRequiredPage.value.contains(true)
    val isAlwaysRequired = GuarantorRequiredPage.isRequired()
    val showSummaryRow = guarantorRequiredAnswerIsTrue || isAlwaysRequired

    (guarantorArrangerAnswer, showSummaryRow) match {
      case (Some(arranger), true) => Some(renderRow(arranger))
      case _ => None
    }
  }

  def renderRow(arranger: GuarantorArranger)(implicit request: DataRequest[_], messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "guarantorName.checkYourAnswersLabel",
      value = ValueViewModel(businessName(arranger)),
      actions = if (!showChangeLink(arranger)) {
        Seq()
      } else {
        val mode = if (GuarantorNamePage.value.nonEmpty) CheckMode else NormalMode
        Seq(
          ActionItemViewModel(
            "site.change",
            controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(request.ern, request.draftId, mode).url,
            "changeGuarantorName"
          ).withVisuallyHiddenText(messages("guarantorName.change.hidden"))
        )
      }
    )

  private def showChangeLink(arranger: GuarantorArranger): Boolean = arranger == GoodsOwner || arranger == Transporter

  private def businessName(arranger: GuarantorArranger)(implicit request: DataRequest[_], messages: Messages): String = arranger match {
    case Consignor => request.traderKnownFacts.traderName
    case Consignee => ConsigneeBusinessNamePage.value match {
      case Some(answer) => HtmlFormat.escape(answer).toString()
      case None => messages("guarantorName.checkYourAnswers.notProvided", messages(s"guarantorArranger.$arranger"))
    }
    case _ =>
      GuarantorNamePage.value match {
        case Some(answer) => HtmlFormat.escape(answer).toString()
        case None => messages("site.notProvided")
      }
  }

}
