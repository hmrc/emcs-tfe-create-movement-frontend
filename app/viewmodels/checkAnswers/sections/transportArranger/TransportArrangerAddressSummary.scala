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

import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import models.{CheckMode, UserAddress}
import pages.QuestionPage
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportArrangerAddressSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val transportArranger = TransportArrangerPage.value

    val addressPage: QuestionPage[UserAddress] = transportArranger match {
      case Some(Consignor) => ConsignorAddressPage()
      case Some(Consignee) => ConsigneeAddressPage
      case _ => TransportArrangerAddressPage
    }

    val showChangeLink: Boolean = transportArranger.contains(GoodsOwner) || transportArranger.contains(Other)

    val value: Content = addressPage.value.fold[Content] {
      transportArranger match {
        case Some(Consignor) => Text(messages("transportArrangerAddress.checkYourAnswers.notProvided", messages(s"transportArranger.$Consignor")))
        case Some(Consignee) => Text(messages("transportArrangerAddress.checkYourAnswers.notProvided", messages(s"transportArranger.$Consignee")))
        case _ => Text(messages("site.notProvided"))
      }
    }(_.toCheckYourAnswersFormat)

    SummaryListRowViewModel(
      key = "transportArrangerAddress.checkYourAnswers.label",
      value = ValueViewModel(value),
      actions = if (!showChangeLink) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(
            ern = request.userAnswers.ern,
            draftId = request.userAnswers.draftId,
            mode = CheckMode
          ).url,
          id = "changeTransportArrangerAddress"
        ).withVisuallyHiddenText(messages("transportArrangerAddress.change.hidden"))
      )
    )
  }
}
