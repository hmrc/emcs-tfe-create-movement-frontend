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

package viewmodels.checkAnswers.sections.items

import controllers.sections.items.routes
import models.requests.DataRequest
import models.{CheckMode, Index, UnitOfMeasure}
import pages.sections.items.ItemQuantityPage
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import viewmodels.implicits._

import javax.inject.Inject

class ItemQuantitySummary @Inject()(tagHelper: TagHelper) {

  def row(idx: Index, unitOfMeasure: UnitOfMeasure, showChangeLinks: Boolean = true)
         (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    lazy val page = ItemQuantityPage(idx)
    lazy val hasUnfixedQuantityError = page.isMovementSubmissionError

    request.userAnswers.get(page).map {
      answer =>
        SummaryListRowViewModel(
          key = s"$page.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
            Some(Html(messages(s"$page.checkYourAnswersValue", answer, unitOfMeasure.toShortFormatMessage()))),
            if(hasUnfixedQuantityError) Some(tagHelper.updateNeededTag()) else None
          ).flatten))),
          actions = if (!showChangeLinks) Seq() else Seq(ActionItemViewModel(
            href = routes.ItemQuantityController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
            content = "site.change",
            id = s"changeItemQuantity${idx.displayIndex}"
          ).withVisuallyHiddenText(messages(s"$page.change.hidden")))
        )
    }
  }
}
