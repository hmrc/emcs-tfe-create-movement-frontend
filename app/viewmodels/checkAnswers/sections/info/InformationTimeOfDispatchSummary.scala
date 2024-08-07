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

package viewmodels.checkAnswers.sections.info

import models.CheckMode
import models.requests.DataRequest
import models.sections.info.DispatchDetailsModel
import pages.sections.info.DispatchDetailsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.DateTimeUtils
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object InformationTimeOfDispatchSummary extends DateTimeUtils {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    val data: Option[DispatchDetailsModel] = DispatchDetailsPage().value

    val value: String = data match {
      case Some(dispatchDetailsPage) => dispatchDetailsPage.time.formatTimeForUIOutput
      case None => messages("site.notProvided")
    }
    Some(
      SummaryListRowViewModel(
        key = "dispatchDetails.time.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            if (isOnPreDraftFlow) {
              controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(request.ern, CheckMode).url
            } else {
              controllers.sections.info.routes.DispatchDetailsController.onPageLoad(request.ern, request.draftId, CheckMode).url
            },
            id = "changeTimeOfDispatch")
            .withVisuallyHiddenText(messages("dispatchDetails.time.change.hidden"))
        )
      )
    )
  }


}
