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
import models.{CheckMode, Index, UserAnswers}
import pages.sections.items.{ItemAlcoholStrengthPage, ItemBrandNamePage, ItemCommodityCodePage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.sections.items.ItemBrandNameSummary.getValue
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemCommodityCodeSummary {
  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    request.userAnswers.get(ItemCommodityCodePage(idx)).map { value =>
      SummaryListRowViewModel(
        key = "itemCommodityCode.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            content = "site.change",
            routes.ItemCommodityCodeController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
            id = s"changeItemBrandName${idx.displayIndex}"
          )
            .withVisuallyHiddenText(messages("itemCommodityCode.change.hidden"))
        )
      )
    }
  }
}
