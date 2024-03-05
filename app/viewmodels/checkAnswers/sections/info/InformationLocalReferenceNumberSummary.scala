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
import pages.sections.info.LocalReferenceNumberPage
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.taskList.UpdateNeeded

import javax.inject.Inject

class InformationLocalReferenceNumberSummary @Inject()(tag: views.html.components.tag) {

  def row(deferredMovement: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    val hasUnfixedLRNError = LocalReferenceNumberPage(deferredMovement).isMovementSubmissionError

    request.userAnswers.get(LocalReferenceNumberPage()).map { lrn =>

      val deferredType: String = if (deferredMovement) "deferred" else "new"

      SummaryListRowViewModel(
        key = s"localReferenceNumber.$deferredType.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
          Some(HtmlFormat.escape(lrn)),
          if(hasUnfixedLRNError) Some(updateNeededTag()) else None
        ).flatten))),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            if (isOnPreDraftFlow) {
              controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftPageLoad(ern = request.ern, CheckMode).url
            } else {
              controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(request.ern, request.draftId).url
            },
            id = "changeLocalReferenceNumber")
            .withVisuallyHiddenText(messages(s"localReferenceNumber.$deferredType.change.hidden"))
        )
      )
    }
  }

  private def updateNeededTag()(implicit messages: Messages): Html = tag(
    message = messages(UpdateNeeded.msgKey),
    colour = "orange",
    extraClasses = "float-none govuk-!-margin-left-1"
  )
}
