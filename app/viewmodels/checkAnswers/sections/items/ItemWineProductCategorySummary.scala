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
import pages.sections.items.ItemWineProductCategoryPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemWineProductCategorySummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val page = ItemWineProductCategoryPage(idx)

    page.value.map { answer =>

      val value: String = messages(s"itemWineProductCategory.$answer")

      SummaryListRowViewModel(
        key = "itemWineProductCategory.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            content = "site.change",
            href = controllers.sections.items.routes.ItemWineProductCategoryController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
            id = s"changeItemWineProductCategory${idx.displayIndex}"
          ).withVisuallyHiddenText(messages("itemWineProductCategory.change.hidden"))
        )
      )

    }
  }
}
