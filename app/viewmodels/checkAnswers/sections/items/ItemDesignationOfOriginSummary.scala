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
import pages.sections.items.ItemDesignationOfOriginPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemDesignationOfOriginSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ItemDesignationOfOriginPage).map {
      answer =>

        val value = ValueViewModel(
          HtmlContent(
            HtmlFormat.escape(messages(s"itemDesignationOfOrigin.$answer"))
          )
        )

        SummaryListRowViewModel(
          key     = "itemDesignationOfOrigin.checkYourAnswersLabel",
          value   = value,
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.items.routes.ItemDesignationOfOriginController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              id = s"changeItemDesignationOfOrigin${idx.displayIndex}"
            ).withVisuallyHiddenText(messages("itemDesignationOfOrigin.change.hidden"))
          )
        )
    }
}
