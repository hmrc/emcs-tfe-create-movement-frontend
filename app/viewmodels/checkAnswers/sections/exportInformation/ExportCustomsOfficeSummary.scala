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

package viewmodels.checkAnswers.sections.exportInformation

import models.CheckMode
import models.requests.DataRequest
import pages.sections.exportInformation.ExportCustomsOfficePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import viewmodels.implicits._

import javax.inject.Inject

class ExportCustomsOfficeSummary @Inject()(tagHelper: TagHelper) {

  def row(showActionLinks: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =

    ExportCustomsOfficePage.value.map {
      answer =>

        val hasUnfixedExportCustomsOfficeNumberError = ExportCustomsOfficePage.isMovementSubmissionError

        SummaryListRowViewModel(
          key = "exportCustomsOffice.checkYourAnswers.label",
          value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
            Some(HtmlFormat.escape(answer)),
            if(hasUnfixedExportCustomsOfficeNumberError) Some(tagHelper.updateNeededTag()) else None
          ).flatten))),
          actions = if (!showActionLinks) Seq() else Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(
                ern = request.userAnswers.ern,
                draftId = request.userAnswers.draftId,
                mode = CheckMode
              ).url,
              id = "changeExportCustomsOffice"
            )
              .withVisuallyHiddenText(messages("exportCustomsOffice.change.hidden"))
          )
        )
    }
}
