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
import pages.sections.items.ItemExciseProductCodePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import javax.inject.Inject

class ItemExciseProductCodeSummary @Inject()(p: views.html.components.p) {

  def row(idx: Index, description: Option[String])(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val answers = request.userAnswers
    answers.get(ItemExciseProductCodePage(idx)).map {
      code =>
        SummaryListRowViewModel(
          key = "itemExciseProductCode.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
            p()(HtmlFormat.escape(code)),
            p()(HtmlFormat.escape(description.get))
          )))),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(answers.ern, answers.draftId, idx, CheckMode).url,
              id = s"itemExciseProductCode${idx.displayIndex}"
            ).withVisuallyHiddenText(messages("itemExciseProductCode.change.hidden"))
          )
        )
    }
  }
}
