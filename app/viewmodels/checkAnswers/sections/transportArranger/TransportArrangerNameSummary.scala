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

package viewmodels.checkAnswers.sections.transportArranger

import models.CheckMode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportArrangerNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val transportArranger: Option[TransportArranger] = TransportArrangerPage.value

    val showChangeLink: Boolean = transportArranger.contains(GoodsOwner) || transportArranger.contains(Other)

    SummaryListRowViewModel(
      key = "transportArrangerName.checkYourAnswers.label",
      value = ValueViewModel(Text(transportArrangerNameValue(transportArranger))),
      actions = if (!showChangeLink) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(
            ern = request.userAnswers.ern,
            draftId = request.userAnswers.draftId,
            mode = CheckMode
          ).url,
          id = "changeTransportArrangerName"
        )
          .withVisuallyHiddenText(messages("transportArrangerName.change.hidden"))
      )
    )
  }

  private[transportArranger] def transportArrangerNameValue(transportArranger: Option[TransportArranger])
                                                           (implicit request: DataRequest[_], messages: Messages): String = {
    transportArranger match {
      case Some(Consignor) => ConsignorAddressPage.value.map(address => HtmlFormat.escape(address.businessName.getOrElse("")).toString()).getOrElse(
        messages("transportArrangerName.checkYourAnswers.notProvided", messages(s"transportArranger.$Consignor"))
      )
      case Some(Consignee) => ConsigneeAddressPage.value.map(address => HtmlFormat.escape(address.businessName.getOrElse("")).toString()).getOrElse(
        messages("transportArrangerName.checkYourAnswers.notProvided", messages(s"transportArranger.$Consignee"))
      )
      case Some(arranger) => TransportArrangerAddressPage.value.map(address => HtmlFormat.escape(address.businessName.getOrElse("")).toString()).getOrElse(
        messages("transportArrangerName.checkYourAnswers.notProvided", messages(s"transportArranger.$arranger"))
      )
      case _ => TransportArrangerAddressPage.value.map(address => HtmlFormat.escape(address.businessName.getOrElse("")).toString()).getOrElse(messages("site.notProvided"))
    }
  }
}
