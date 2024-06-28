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

import models.requests.DataRequest
import models.{CheckMode, NorthernIrelandTemporaryCertifiedConsignor}
import pages.sections.consignor.ConsignorPaidTemporaryAuthorisationCodePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ConsignorPaidTemporaryAuthorisationCodeSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ConsignorPaidTemporaryAuthorisationCodePage).flatMap { ptaCode =>
      Option.when(request.userTypeFromErn == NorthernIrelandTemporaryCertifiedConsignor) {
        SummaryListRowViewModel(
          key = "checkYourAnswersConsignor.paidTemporaryAuthorisationCode",
          value = ValueViewModel(HtmlFormat.escape(ptaCode).toString),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.consignor.routes.ConsignorPaidTemporaryAuthorisationCodeController.onPageLoad(request.ern, request.draftId, CheckMode).url,
              id = "changeConsignorPaidTemporaryAuthorisationCode"
            ).withVisuallyHiddenText(messages("checkYourAnswersConsignor.paidTemporaryAuthorisationCode.change.hidden"))
          )
        )
      }
    }
}
