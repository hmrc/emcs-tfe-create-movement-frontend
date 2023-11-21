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
import pages.sections.items.ItemSmallIndependentProducerPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemSmallIndependentProducerSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(ItemSmallIndependentProducerPage(idx)).map { value =>

      val answer = if (value) "site.yes" else "site.no"

      SummaryListRowViewModel(
        key = "itemSmallIndependentProducer.checkYourAnswersLabel",
        value = ValueViewModel(messages(answer)),
        actions =
          Seq(
            ActionItemViewModel(
              content = "site.change",
              routes.ItemSmallIndependentProducerController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
              id = s"changeItemSmallIndependentProducer${idx.displayIndex}"
            ).withVisuallyHiddenText(messages("itemSmallIndependentProducer.change.hidden"))
          )
      )
    }
  }
}
