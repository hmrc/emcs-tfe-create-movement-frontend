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

package viewmodels.checkAnswers.sections.destination

import models.CheckMode
import models.requests.DataRequest
import pages.sections.destination.DestinationBusinessNamePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DestinationBusinessNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val value = request.userAnswers.get(DestinationBusinessNamePage).getOrElse(messages("destinationCheckAnswers.destination.notProvided"))

    SummaryListRowViewModel(
      key = "destinationBusinessName.checkYourAnswersLabel",
      value = ValueViewModel(HtmlFormat.escape(value).toString),
      actions = Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(request.ern, request.draftId, CheckMode).url,
          id = "changeDestinationBusinessName"
        ).withVisuallyHiddenText(messages("destinationBusinessName.change.hidden"))
      )
    )
  }
}
