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

import controllers.sections.destination.routes
import models.CheckMode
import models.requests.DataRequest
import pages.sections.destination.DestinationConsigneeDetailsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DestinationConsigneeDetailsSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    DestinationConsigneeDetailsPage.value.map { answer =>

      val value = if (answer) "site.yes" else "site.no"

      SummaryListRowViewModel(
        key = "destinationConsigneeDetails.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            content = "site.change",
            href = routes.DestinationConsigneeDetailsController.onPageLoad(request.ern, request.draftId, CheckMode).url,
            id = "changeDestinationConsigneeDetails"
          ).withVisuallyHiddenText(messages("destinationConsigneeDetails.change.hidden"))
        )
      )
    }
}
