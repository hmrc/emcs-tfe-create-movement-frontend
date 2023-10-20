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

import models.{CheckMode, UserAnswers}
import pages.sections.transportUnit.TransportUnitGiveMoreInformationPage
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import javax.inject.Inject

class TransportUnitGiveMoreInformationSummary @Inject()(link: views.html.components.link) {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    val optMoreInformation = answers.get(TransportUnitGiveMoreInformationPage)
    Some(SummaryListRowViewModel(
      key = "transportUnitGiveMoreInformation.checkYourAnswersLabel",
      value = ValueViewModel(getValue(optMoreInformation,
        controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(answers.ern, answers.lrn, CheckMode))),
      actions = {
        if(optMoreInformation.isEmpty) Seq() else Seq(
          ActionItemViewModel(
            content = "site.change",
            href = controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(answers.ern, answers.lrn, CheckMode).url,
            id = "transportUnitMoreInformation"
          ).withVisuallyHiddenText(messages("transportUnitGiveMoreInformation.change.hidden"))
        )
      }
    ))
  }

  private def getValue(optValue: Option[String], redirectUrl: Call)(implicit messages: Messages): Content = {
    if(optValue.isEmpty) {
      HtmlContent(link(redirectUrl.url, messages("transportUnitGiveMoreInformation.checkYourAnswersValue")))
    } else {
      Text(optValue.get)
    }
  }

}
