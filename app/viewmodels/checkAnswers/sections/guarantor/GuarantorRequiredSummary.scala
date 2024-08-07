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
import pages.sections.guarantor.GuarantorRequiredPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorRequiredSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    Option.when(!GuarantorRequiredPage.isRequired())(renderSummary)

  private def renderSummary(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val value = GuarantorRequiredPage.value match {
      case Some(answer) => if (answer) "site.yes" else "site.no"
      case None => "site.notProvided"
    }

    SummaryListRowViewModel(
      key = "guarantorRequired.checkYourAnswersLabel",
      value = ValueViewModel(value),
      actions = Seq(
        ActionItemViewModel(
          "site.change",
          controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(request.ern, request.draftId, CheckMode).url,
          "changeGuarantorRequired"
        )
          .withVisuallyHiddenText(messages("guarantorRequired.change.hidden"))
      )
    )
  }

}
