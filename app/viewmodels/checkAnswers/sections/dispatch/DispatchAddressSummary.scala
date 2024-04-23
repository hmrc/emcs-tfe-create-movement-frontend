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
import pages.sections.dispatch.DispatchAddressPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


object DispatchAddressSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow =
    request.userAnswers.get(DispatchAddressPage) match {
      case Some(address) => renderRow(ValueViewModel(address.toCheckYourAnswersFormat))
      case _ => renderRow(ValueViewModel(Text(messages("site.notProvided"))))
    }

  private def renderRow(value: Value)(implicit request: DataRequest[_], messages: Messages) =
    SummaryListRowViewModel(
      key = "address.dispatchAddress.checkYourAnswersLabel",
      value = value,
      actions = Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(request.ern, request.draftId, CheckMode).url,
          id = "changeDispatchAddress"
        ).withVisuallyHiddenText(messages("address.dispatchAddress.change.hidden"))
      )
    )
}
