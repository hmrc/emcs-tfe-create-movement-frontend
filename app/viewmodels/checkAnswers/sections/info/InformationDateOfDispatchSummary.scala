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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object InformationDateOfDispatchSummary {

  def row()(implicit messages: Messages): Option[SummaryListRow] = {

    val data: Option[String] = None

    val value: String = data match {
      case Some(answer) => messages(s"dispatchDetails.$answer")
      case None => messages("site.notProvided")
    }

    Some(
      SummaryListRowViewModel(
        key = "dispatchDetails.dateOfDispatch.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
            id = "changeDateOfDispatch")
            .withVisuallyHiddenText(messages("dispatchDetails.dateOfDispatch.change.hidden"))
        )
      )
    )
  }


}
