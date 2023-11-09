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

import controllers.sections.items.routes
import models.requests.DataRequest
import models.sections.items.ItemDegreesPlatoModel
import models.{CheckMode, Index}
import pages.sections.items.ItemDegreesPlatoPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemDegreesPlatoSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[Seq[SummaryListRow]] =
    request.userAnswers.get(ItemDegreesPlatoPage(idx)).map {
      case ItemDegreesPlatoModel(true, Some(amount)) =>
        Seq(
          radioRow(idx, hasDegreesPlato = true),
          SummaryListRowViewModel(
            key = "itemDegreesPlato.amount.checkYourAnswersLabel",
            value = ValueViewModel(s"$amount ${messages("itemDegreesPlato.suffix")}"),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
                s"changeItemDegreesPlatoAmount${idx.displayIndex}"
              )
                .withVisuallyHiddenText(messages("itemDegreesPlato.change.hidden"))
            )
          )
        )
      case _ =>
        Seq(radioRow(idx, hasDegreesPlato = false))
    }

  private def radioRow(idx: Index, hasDegreesPlato: Boolean)(implicit request: DataRequest[_], messages: Messages) =
    SummaryListRowViewModel(
      key = "itemDegreesPlato.radio.checkYourAnswersLabel",
      value = ValueViewModel(if (hasDegreesPlato) "site.yes" else "site.no"),
      actions = Seq(
        ActionItemViewModel(
          "site.change",
          routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
          s"changeItemDegreesPlatoRadio${idx.displayIndex}"
        )
          .withVisuallyHiddenText(messages("itemDegreesPlato.change.hidden"))
      )
    )
}
