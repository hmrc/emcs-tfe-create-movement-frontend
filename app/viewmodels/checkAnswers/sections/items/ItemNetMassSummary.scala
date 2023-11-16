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

import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.items.ItemNetGrossMassPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemNetMassSummary  {

  def row(idx: Index)(implicit messages: Messages, request: DataRequest[_]): Option[SummaryListRow] =
    request.userAnswers.get(ItemNetGrossMassPage(idx)).map {
      answer =>
        SummaryListRowViewModel(
          key     = "itemNetGrossMass.netMase.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlFormat.escape(answer.netMass.toString()).toString),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.sections.items.routes.ItemNetGrossMassController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
              "changeNetMass"
            )
              .withVisuallyHiddenText(messages("itemNetGrossMass.netMass.change.hidden"))
          )
        )
    }
}
