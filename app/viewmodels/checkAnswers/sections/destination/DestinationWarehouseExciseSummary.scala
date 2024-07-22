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
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import viewmodels.implicits._

import javax.inject.Inject

class DestinationWarehouseExciseSummary @Inject()(tagHelper: TagHelper) {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    DestinationWarehouseExcisePage.value.map {
      answer =>

        val hasUnfixedError = DestinationWarehouseExcisePage.isMovementSubmissionError

        val summaryListRowViewValue = ValueViewModel(
          HtmlContent(
            HtmlFormat.fill(Seq(
              Some(HtmlFormat.escape(answer)),
              if (hasUnfixedError) Some(tagHelper.updateNeededTag()) else None
            ).flatten)
          )
        )

        ValueViewModel(HtmlFormat.escape(answer).toString)

        SummaryListRowViewModel(
          key = "destinationWarehouseExcise.checkYourAnswersLabel",
          value = summaryListRowViewValue,
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(
                ern = request.ern,
                draftId = request.draftId,
                mode = CheckMode
              ).url,
              id = "changeDestinationWarehouseExcise")
              .withVisuallyHiddenText(messages("destinationWarehouseExcise.change.hidden"))
          )
        )
    }
}
