/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.consignor

import models.{CheckMode, UserAddress}
import models.requests.DataRequest
import pages.sections.consignor.ConsignorAddressPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ConsignorTraderNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = ConsignorAddressPage.value.map {
    case UserAddress(businessName, _, _, _, _) =>
      SummaryListRowViewModel(
        key = "checkYourAnswersConsignor.traderName",
        value = ValueViewModel(HtmlFormat.escape(businessName.getOrElse("")).toString),
        actions = if (request.traderKnownFacts.isDefined) Seq() else Seq(
          ActionItemViewModel(
            content = "site.change",
            href = controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(request.ern, request.draftId, CheckMode).url,
            id = "changeConsignorName"
          ).withVisuallyHiddenText(messages("checkYourAnswersConsignor.businessName.change.hidden"))
        )
      )
  }
}
