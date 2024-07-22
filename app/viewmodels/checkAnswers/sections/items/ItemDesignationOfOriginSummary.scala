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

package viewmodels.checkAnswers.sections.items

import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.items.ItemDesignationOfOriginPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.p

import javax.inject.Inject

class ItemDesignationOfOriginSummary @Inject()(p: p) {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    ItemDesignationOfOriginPage(idx).value.map {
      answer =>

        val marketingAndLabellingAnswer = answer.isSpiritMarketedAndLabelled.map(isSpiritMarketedAndLabelled =>
          if(isSpiritMarketedAndLabelled) messages("itemDesignationOfOrigin.s200.radio.yes") else messages("itemDesignationOfOrigin.s200.radio.unprovided")
        )

        val content = Seq(
          Some(p()(Text(messages(s"itemDesignationOfOrigin.${answer.geographicalIndication}")).asHtml)),
          answer.geographicalIndicationIdentification.map(identification => p()(Text(identification).asHtml)),
          marketingAndLabellingAnswer.map(marketingAndLabelling => p()(Text(marketingAndLabelling).asHtml))
        ).flatten.mkString

        SummaryListRowViewModel(
          key     = if(answer.isSpiritMarketedAndLabelled.isDefined) "itemDesignationOfOrigin.checkYourAnswersLabel.s200" else "itemDesignationOfOrigin.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlContent(content)),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.items.routes.ItemDesignationOfOriginController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              id = s"changeItemDesignationOfOrigin${idx.displayIndex}"
            ).withVisuallyHiddenText(messages(if(answer.isSpiritMarketedAndLabelled.isDefined) "itemDesignationOfOrigin.change.hidden.s200" else "itemDesignationOfOrigin.change.hidden"))
          )
        )
    }
}
