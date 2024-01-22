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
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformationType._
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import pages.sections.consignee.{ConsigneeExcisePage, ConsigneeExportInformationPage}
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorVatPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorErnVatEoriSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(GuarantorRequiredPage).filter(required => required).flatMap { _ =>
      request.userAnswers.get(GuarantorArrangerPage)
        .map { arranger =>

          val (summaryListKey, summaryListValue) = arranger match {
            case Consignor => getConsignorSummary()
            case Consignee => getConsigneeSummary()
            case GoodsOwner => getGuarantorVatSummary()
            case Transporter => getGuarantorVatSummary()
          }

          val changeAction = arranger match {
            case GoodsOwner | Transporter =>
              Seq(
                ActionItemViewModel(
                  "site.change",
                  controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(request.ern, request.draftId, CheckMode).url,
                  "changeGuarantorVat"
                ).withVisuallyHiddenText(messages("guarantorVat.change.hidden"))
              )
            case _ =>
              Seq().empty
          }

          SummaryListRowViewModel(
            key = summaryListKey,
            value = ValueViewModel(summaryListValue),
            actions = changeAction
          )
        }
    }
  }

  private def getConsignorSummary()(implicit request: DataRequest[_]): (String, String) =
    "guarantorErn.checkYourAnswersLabel" -> request.ern

  private def getConsigneeSummary()(implicit request: DataRequest[_], messages: Messages): (String, String) =
    (
      request.userAnswers.get(ConsigneeExcisePage),
      request.userAnswers.get(ConsigneeExportInformationPage)
    ) match {
      case (Some(ern), _) =>
        "guarantorErn.checkYourAnswersLabel" -> HtmlFormat.escape(ern).toString()
      case (None, Some(ConsigneeExportInformation(YesVatNumber, Some(vatNumber), _))) =>
        "guarantorVat.checkYourAnswersLabel" -> HtmlFormat.escape(vatNumber).toString()
      case (None, Some(ConsigneeExportInformation(YesEoriNumber, _, Some(eoriNumber)))) =>
        "consigneeExportInformation.eoriNumber.label" -> HtmlFormat.escape(eoriNumber).toString()
      case (None, Some(ConsigneeExportInformation(No, _, _))) =>
        "consigneeExportInformation.checkYourAnswers.label.notKnown" -> "consigneeExportInformation.checkYourAnswers.value.notKnown"
      case (None, None) =>
        "guarantorErn.checkYourAnswersLabel" -> messages("guarantorErn.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee"))
    }

  private def getGuarantorVatSummary()(implicit request: DataRequest[_]): (String, String) =
    request.userAnswers.get(GuarantorVatPage) match {
      case Some(vatNumber) => "guarantorVat.checkYourAnswersLabel" -> HtmlFormat.escape(vatNumber).toString()
      case None => "guarantorVat.checkYourAnswersLabel" -> "site.notProvided"
    }

}
