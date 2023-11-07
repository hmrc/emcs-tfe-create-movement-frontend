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


import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.items.CommercialDescriptionPage
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object CommercialDescriptionSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages, link: views.html.components.link): Option[SummaryListRow] = {

  val commercialDescription = request.userAnswers.get(CommercialDescriptionPage(idx))

  Some(SummaryListRowViewModel(
    key = "commercialDescription.checkYourAnswersLabel",
    value = ValueViewModel(getValue(commercialDescription,
      controllers.sections.items.routes.CommercialDescriptionController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode))),
    actions = {
      Seq(
        commercialDescription.map(_ =>
        ActionItemViewModel("site.change", controllers.sections.items.routes.CommercialDescriptionController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, CheckMode).url,
          id = s"changeTransportUnitMoreInformation${idx.displayIndex}")
          .withVisuallyHiddenText(messages("commercialDescription.change.hidden"))
        )
      ).flatten
    }
  ))
}
  private def getValue(optValue: Option[String], redirectUrl: Call)(implicit messages: Messages, link: views.html.components.link): Content =
    optValue.fold[Content](HtmlContent(link(redirectUrl.url, messages("commercialDescription.checkYourAnswersValue"))))(value => Text(value))

}
