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

package viewmodels.checkAnswers.sections.transportArranger

import models.CheckMode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor}
import pages.QuestionPage
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.transportArranger.{TransportArrangerNamePage, TransportArrangerPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportArrangerNameSummary {

  def row(showActionLinks: Boolean)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val transportArranger = request.userAnswers.get(TransportArrangerPage)

    val namePage: QuestionPage[String] = transportArranger match {
      //case Some(Consignor) => ConsignorBusinessNamePage //TODO: Add when Consignor Business Name Page is built
      case Some(Consignee) => ConsigneeBusinessNamePage
      case _ => TransportArrangerNamePage
    }

    val showChangeLink = if (transportArranger.contains(Consignor) || transportArranger.contains(Consignee)) false else showActionLinks

    SummaryListRowViewModel(
      key = "transportArrangerName.checkYourAnswers.label",
      value = ValueViewModel(Text(request.userAnswers.get(namePage).getOrElse(messages("site.notProvided")))),
      actions = if (!showChangeLink) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(
            ern = request.userAnswers.ern,
            lrn = request.userAnswers.lrn,
            mode = CheckMode
          ).url,
          id = "changeTransportArrangerName"
        )
          .withVisuallyHiddenText(messages("transportArrangerName.change.hidden"))
      )
    )
  }
}
