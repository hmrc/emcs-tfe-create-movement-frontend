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

package viewmodels.checkAnswers.sections.transportUnit

import controllers.sections.transportUnit.routes
import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.transportUnit.TransportSealChoicePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportSealChoiceSummary {

  def row(idx: Index, sectionComplete: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    Some(
      SummaryListRowViewModel(
        key = "transportSealChoice.checkYourAnswersLabel",
        value = ValueViewModel(getValue(idx)),
        actions = if (sectionComplete) {
          Seq(
            ActionItemViewModel(
              content = "site.change",
              href = routes.TransportSealChoiceController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
              id = s"changeTransportSealChoice${idx.displayIndex}")
              .withVisuallyHiddenText(messages("transportSealChoice.change.hidden", idx.displayIndex))
          )
        } else {
          Seq()
        }
      ))


  private def getValue(idx: Index)(implicit request: DataRequest[_], messages: Messages): Content =
    TransportSealChoicePage(idx).value.fold(Text(messages("site.notProvided")))(answer => if (answer) "site.yes" else "site.no")
}
