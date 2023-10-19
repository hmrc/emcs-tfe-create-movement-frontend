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
import models.{CheckMode, UserAnswers}
import pages.sections.transportUnit.TransportSealTypePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportSealTypeSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(TransportSealTypePage).map {
      answer =>
        SummaryListRowViewModel(
          key     = "transportSealType.sealType.checkYourAnswersLabel",
          value   = ValueViewModel(answer.sealType),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.TransportSealTypeController.onPageLoad(answers.ern, answers.lrn, CheckMode).url,
              "changeTransportSealType"
            ).withVisuallyHiddenText(messages("transportSealType.sealType.change.hidden"))
          )
        )
    }
}