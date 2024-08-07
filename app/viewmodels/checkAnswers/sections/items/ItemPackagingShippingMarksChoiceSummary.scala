/*
 * Copyright 2024 HM Revenue & Customs
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
import pages.sections.items.{ItemPackagingQuantityPage, ItemPackagingShippingMarksChoicePage, ItemsPackagingSectionItems}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemPackagingShippingMarksChoiceSummary {

  def row(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    ItemPackagingShippingMarksChoicePage(itemIdx, packagingIdx).value.map {
      answer =>

        val isPackagingQuantityEqualToZero = ItemPackagingQuantityPage(itemIdx, packagingIdx).value.exists(BigInt(_) == 0)

        val value = (answer, isPackagingQuantityEqualToZero) match {
          case (true, true) => "itemPackagingShippingMarksChoice.checkYourAnswersLabel.yes.existing"
          case (true, _) => "site.yes"
          case (false, _) => "site.no"
        }

        SummaryListRowViewModel(
          key     = "itemPackagingShippingMarksChoice.checkYourAnswersLabel",
          value   = ValueViewModel(value),
          actions = if(!ItemsPackagingSectionItems(itemIdx, packagingIdx).isCompleted) Seq() else Seq(
            ActionItemViewModel(
              "site.change",
              controllers.sections.items.routes.ItemPackagingShippingMarksChoiceController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, itemIdx, packagingIdx, CheckMode).url,
              id = s"changeItemPackagingShippingMarksChoice${packagingIdx.displayIndex}ForItem${itemIdx.displayIndex}"
            ).withVisuallyHiddenText(messages("itemPackagingShippingMarksChoice.change.hidden"))
          )
        )
    }
}
