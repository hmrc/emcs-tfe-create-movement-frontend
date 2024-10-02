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
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import utils.DateTimeUtils
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import viewmodels.implicits._

import javax.inject.Inject
import views.html.components

class InformationDateOfDispatchSummary @Inject()(link: components.link,
                                                 tagHelper: TagHelper) extends DateTimeUtils {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    val hasUnfixedError = DispatchDetailsPage(isOnPreDraftFlow = false).isMovementSubmissionError

    val data: Option[DispatchDetailsModel] = DispatchDetailsPage().value

    val changeLink = if (isOnPreDraftFlow) {
      controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(request.ern, CheckMode)
    } else {
      controllers.sections.info.routes.DispatchDetailsController.onPageLoad(request.ern, request.draftId, CheckMode)
    }

    val value: Html = data match {
      case Some(dispatchDetailsPage) => HtmlFormat.escape(dispatchDetailsPage.date.formatDateForUIOutput())
      case None => link(
        link = changeLink.url,
        messageKey = messages("dispatchDetails.date.add"),
        id = Some("changeDateOfDispatch")
      )
    }

    Some(
      SummaryListRowViewModel(
        key = "dispatchDetails.value.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
          Some(value),
          if (hasUnfixedError) Some(tagHelper.updateNeededTag(withNoFloat = false)) else None
        ).flatten))),
        actions =
          (data, isOnPreDraftFlow) match {
            case (None, true) =>
              Seq()
            case (None, false) =>
              Seq(ActionItem(
                content = HtmlContent(tagHelper.incompleteTag(withNoFloat = true)),
                href = changeLink.url,
                visuallyHiddenText = Some(messages("dispatchDetails.date.add")),
                classes = "cursor-default",
                attributes = Map("tabindex" -> "-1")
              ))
            case (Some(_), _) =>
              Seq(ActionItemViewModel(
                content = "site.change",
                href = changeLink.url,
                id = "changeDateOfDispatch"
              ).withVisuallyHiddenText(messages("dispatchDetails.value.change.hidden")))
          }
      )
    )
  }

}
