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
import pages.sections.items.ItemDegreesPlatoPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemDegreesPlatoSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    lazy val page = ItemDegreesPlatoPage(idx)

    def row(value: String = "site.no"): SummaryListRow = SummaryListRowViewModel(
      key = s"$page.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(messages(value))),
      actions = Seq(ActionItemViewModel(
        href = routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
        content = "site.change",
        id = s"changeItemDegreesPlatoAmount${idx.displayIndex}"
      ).withVisuallyHiddenText(messages(s"$page.change.hidden")))
    )

    request.userAnswers.get(page).map {
      answer =>
        if (answer.hasDegreesPlato) {
          answer.degreesPlato
            .map(degreesPlato => row(messages(s"$page.checkYourAnswersValue", degreesPlato)))
            .getOrElse(row())
        } else {
          row()
        }
    }
  }
}
