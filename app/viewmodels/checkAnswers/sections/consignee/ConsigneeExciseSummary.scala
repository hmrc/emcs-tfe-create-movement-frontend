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

package viewmodels.checkAnswers.sections.consignee

import models.CheckMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import viewmodels.implicits._

import javax.inject.Inject


class ConsigneeExciseSummary @Inject()(tagHelper: TagHelper) {

  def row(showActionLinks: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val hasUnfixedConsigneeExciseError = ConsigneeExcisePage.isMovementSubmissionError

    DestinationTypePage.value.flatMap { destinationType =>

      val key -> changeHiddenKey = destinationType match {
        case TemporaryRegisteredConsignee =>
          "consigneeExcise.checkYourAnswersLabel.ernForTemporaryRegisteredConsignee" -> "consigneeExcise.change.hidden.ernForTemporaryRegisteredConsignee"
        case TemporaryCertifiedConsignee =>
          "consigneeExcise.checkYourAnswersLabel.ernForTemporaryCertifiedConsignee" -> "consigneeExcise.change.hidden.ernForTemporaryCertifiedConsignee"
        case _ =>
          "consigneeExcise.checkYourAnswersLabel.ern" -> "consigneeExcise.change.hidden.ern"
      }

      ConsigneeExcisePage.value.map { answer =>

        SummaryListRowViewModel(
          key = key,
          value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
            Some(HtmlFormat.escape(answer)),
            if (hasUnfixedConsigneeExciseError) Some(tagHelper.updateNeededTag()) else None
          ).flatten))),
          actions = if (!showActionLinks) Seq() else Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(request.ern, request.draftId, CheckMode).url,
              id = "changeConsigneeExcise"
            ).withVisuallyHiddenText(messages(changeHiddenKey))
          )
        )
      }
    }
  }
}
