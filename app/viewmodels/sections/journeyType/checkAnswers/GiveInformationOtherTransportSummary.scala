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

package viewmodels.sections.journeyType.checkAnswers

import models.{CheckMode, UserAnswers}
import pages.sections.journeyType.GiveInformationOtherTransportPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GiveInformationOtherTransportSummary {

  def row(showActionLinks: Boolean)(implicit answers: UserAnswers, messages: Messages): Option[SummaryListRow] =
    answers.get(GiveInformationOtherTransportPage).map {
      answer =>

        SummaryListRowViewModel(
          key = "giveInformationOtherTransport.checkYourAnswersLabel",
          value = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = if (!showActionLinks) Seq() else Seq(
            ActionItemViewModel(
              "site.change",
              controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(answers.ern, answers.lrn, CheckMode).url,
              GiveInformationOtherTransportPage
            )
              .withVisuallyHiddenText(messages("giveInformationOtherTransport.change.hidden"))
          )
        )
    }
}