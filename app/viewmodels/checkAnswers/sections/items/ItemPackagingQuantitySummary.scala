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
import pages.sections.items.{ItemPackagingQuantityPage, ItemsPackagingSectionItems}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemPackagingQuantitySummary {

  def row(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    ItemPackagingQuantityPage(itemIdx, packagingIdx).value.map {
      value =>
        SummaryListRowViewModel(
          key = "itemPackagingQuantity.checkYourAnswersLabel",
          value = ValueViewModel(HtmlFormat.escape(value).toString()),
          actions = if (!ItemsPackagingSectionItems(itemIdx, packagingIdx).isCompleted) Seq() else Seq(
              ActionItemViewModel(
                content = "site.change",
                routes.ItemPackagingQuantityController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, itemIdx, packagingIdx, CheckMode).url,
                id = s"changeItemPackagingQuantity${packagingIdx.displayIndex}ForItem${itemIdx.displayIndex}"
              ).withVisuallyHiddenText(messages("itemPackagingQuantity.change.hidden"))
            )
        )
    }
  }
}
