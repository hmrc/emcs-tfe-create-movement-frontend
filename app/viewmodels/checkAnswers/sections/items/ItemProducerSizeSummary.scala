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
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import models.sections.items.ItemSmallIndependentProducerType.{SelfCertifiedIndependentSmallProducerAndConsignor, SelfCertifiedIndependentSmallProducerAndNotConsignor}
import models.{CheckMode, Index}
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemExciseProductCodePage, ItemProducerSizePage, ItemSmallIndependentProducerPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemProducerSizeSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    lazy val page = ItemProducerSizePage(idx)

    val productType = getProductType(idx)

    for {
      itemSmallIndependentProducer <- request.userAnswers.get(ItemSmallIndependentProducerPage(idx))
      answer <- request.userAnswers.get(page)
      if itemSmallIndependentProducer.producerType == SelfCertifiedIndependentSmallProducerAndConsignor || itemSmallIndependentProducer.producerType == SelfCertifiedIndependentSmallProducerAndNotConsignor
    } yield {
      SummaryListRowViewModel(
        key = s"$page.checkYourAnswersLabel.$productType",
        value = ValueViewModel(messages(s"$page.checkYourAnswersValue", HtmlFormat.escape(answer.toString).toString)),
        actions = Seq(ActionItemViewModel(
          href = routes.ItemProducerSizeController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
          content = "site.change",
          id = s"changeItemProducerSize${idx.displayIndex}"
        ).withVisuallyHiddenText(messages(s"$page.change.$productType.hidden")))
      )
    }
  }

  private def getProductType(idx: Index)(implicit request: DataRequest[_]): String = {
    val destinationType = request.userAnswers.get(DestinationTypePage)
    val itemExciseProductCode = request.userAnswers.get(ItemExciseProductCodePage(idx))
    if(destinationType.contains(UkTaxWarehouse.GB) || itemExciseProductCode.exists(Seq("S300", "S500").contains(_))) {
      "pure"
    } else {
      "finished"
    }
  }
}
