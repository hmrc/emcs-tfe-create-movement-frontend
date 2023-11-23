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
import models.{CheckMode, Index}
import pages.sections.items.ItemMaturationPeriodAgePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemMaturationPeriodAgeSummary  {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    Some(SummaryListRowViewModel(
      key = "itemMaturationPeriodAge.checkYourAnswersLabel",
      value = ValueViewModel(getValue(idx)),
      actions = {
        Seq(
          ActionItemViewModel(
            content = "site.change",
            routes.ItemMaturationPeriodAgeController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
            id = s"changeItemMaturationPeriodAge${idx.displayIndex}"
          ).withVisuallyHiddenText(messages("itemMaturationPeriodAge.change.hidden"))
        )
      }
    ))
  }

  private def getValue(idx: Index)(implicit request: DataRequest[_], messages: Messages): Content =
    request.userAnswers.get(ItemMaturationPeriodAgePage(idx)).flatMap(_.maturationPeriodAge).fold(Text(messages("site.notProvided")))(_.toString())

}
