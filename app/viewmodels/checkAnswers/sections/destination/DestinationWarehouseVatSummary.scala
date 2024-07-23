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

package viewmodels.checkAnswers.sections.destination

import models.CheckMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.destination.DestinationWarehouseVatPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


object DestinationWarehouseVatSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    val wasShownVatPage: Option[Boolean] = DestinationTypePage.value.map(
      Seq(
        RegisteredConsignee,
        TemporaryRegisteredConsignee,
        CertifiedConsignee,
        TemporaryCertifiedConsignee,
        ExemptedOrganisation
      ).contains(_)
    )

    val vatPageAnswer: Option[String] = DestinationWarehouseVatPage.value

    val answer = (wasShownVatPage, vatPageAnswer) match {
      case (_, Some(answer)) => Some(HtmlFormat.escape(answer).toString())
      case (Some(true), _) => Some("site.notProvided")
      case _ => None
    }

    val changeVatLink: Seq[ActionItem] = Seq(
      ActionItemViewModel(
        content = "site.change",
        href = controllers.sections.destination.routes.DestinationWarehouseVatController.onPageLoad(
          ern = request.userAnswers.ern,
          draftId = request.userAnswers.draftId,
          mode = CheckMode
        ).url,
        id = "changeDestinationWarehouseVat"
      ).withVisuallyHiddenText(messages("destinationWarehouseVat.change.hidden"))
    )

    answer.map { value =>
      SummaryListRowViewModel(
        key = "destinationWarehouseVat.checkYourAnswers.label",
        value = ValueViewModel(value),
        actions = changeVatLink
      )
    }
  }
}
