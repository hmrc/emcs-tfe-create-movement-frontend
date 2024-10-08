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
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.{CheckMode, UserAddress}
import pages.QuestionPage
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorAddressPage, GuarantorArrangerPage, GuarantorRequiredPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorAddressSummary {

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

  def renderRow(guarantorArranger: GuarantorArranger)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val addressPage: QuestionPage[UserAddress] = guarantorArranger match {
      case Consignor => ConsignorAddressPage
      case Consignee => ConsigneeAddressPage
      case _ => GuarantorAddressPage
    }

    val showChangeLink: Boolean = guarantorArranger == GoodsOwner || guarantorArranger == Transporter

    val value: Content = addressPage.value.fold[Content] {
      guarantorArranger match {
        case Consignor => Text(messages("address.guarantorAddress.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignor")))
        case Consignee => Text(messages("address.guarantorAddress.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee")))
        case _ => Text(messages("site.notProvided"))
      }
    } { address => address.toCheckYourAnswersFormat }

    SummaryListRowViewModel(
      key = s"address.guarantorAddress.$guarantorArranger.checkYourAnswers.label",
      value = ValueViewModel(value),
      actions = if (!showChangeLink) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(
            ern = request.ern,
            draftId = request.draftId,
            mode = CheckMode
          ).url,
          id = "changeGuarantorAddress"
        )
          .withVisuallyHiddenText(messages(s"address.guarantorAddress.$guarantorArranger.change.hidden"))
      )
    )
  }
}
