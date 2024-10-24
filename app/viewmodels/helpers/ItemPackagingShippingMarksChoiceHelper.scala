/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.helpers

import models.Index
import models.requests.DataRequest
import pages.sections.items.ItemPackagingQuantityPage
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.{RadioItem, Radios}
import viewmodels.LegendSize
import viewmodels.govuk.HintFluency
import viewmodels.govuk.all._

object ItemPackagingShippingMarksChoiceHelper extends HintFluency {

  def options(form: Form[_], itemIndex: Index, packagingIndex: Index)(implicit request: DataRequest[_], messages: Messages): Radios = {
    val isQuantityMoreThanZero: Boolean = ItemPackagingQuantityPage(itemIndex, packagingIndex).value.exists(BigInt(_) > 0)
    val yesMessageKey = if(isQuantityMoreThanZero) "site.yes" else "itemPackagingShippingMarksChoice.choice.yes.existing"
    RadiosViewModel.apply(
      field = form("value"),
      legend = LegendViewModel(Text(messages("itemPackagingShippingMarksChoice.legend"))).withCssClass(LegendSize.Medium.toString),
      items = Seq(
        RadioItem(
          content = Text(messages(yesMessageKey)),
          value = Some("true"),
          id = Some("value")
        ),
        RadioItem(
          content = Text(messages("site.no")),
          value = Some("false"),
          id = Some("value-no"),
          hint = Option.when(!isQuantityMoreThanZero)(HintViewModel(messages("itemPackagingShippingMarksChoice.choice.no.hint", itemIndex.displayIndex)))
        )
      )
    ).withCssClass(if(isQuantityMoreThanZero) "govuk-radios--inline" else "")
  }
}
