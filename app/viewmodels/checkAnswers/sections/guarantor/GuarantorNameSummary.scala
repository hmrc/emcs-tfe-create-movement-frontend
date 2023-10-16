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

import models.CheckMode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger._
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(GuarantorRequiredPage).filter(required => required).flatMap { _ =>

      request.userAnswers.get(GuarantorArrangerPage).map { arranger =>

        val showChangeLink: Boolean = arranger == GoodsOwner || arranger == Transporter

        val businessName: String = arranger match {
          case Consignor => request.traderKnownFacts.traderName
          case Consignee => request.userAnswers.get(ConsigneeBusinessNamePage) match {
            case Some(answer) => HtmlFormat.escape(answer).toString()
            case None => messages("guarantorName.checkYourAnswers.notProvided", messages(s"guarantorArranger.$arranger"))
          }
          case GoodsOwner | Transporter =>
            request.userAnswers.get(GuarantorNamePage) match {
              case Some(answer) => HtmlFormat.escape(answer).toString()
              case None => messages("site.notProvided")
            }
        }

        SummaryListRowViewModel(
          key = "guarantorName.checkYourAnswersLabel",
          value = ValueViewModel(businessName),
          actions = if (!showChangeLink) Seq() else Seq(
            ActionItemViewModel(
              "site.change",
              controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(request.ern, request.draftId, CheckMode).url,
              "changeGuarantorName"
            ).withVisuallyHiddenText(messages("guarantorName.change.hidden"))
          )
        )
      }
    }

  }

}
