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
import models.{CheckMode, Index}
import pages.sections.items.ItemSmallIndependentProducerPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.helpers.ItemSmallIndependentProducerHelper
import viewmodels.implicits._
import views.html.components.p

import javax.inject.Inject

class ItemSmallIndependentProducerSummary @Inject()(p: p) {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    lazy val page = ItemSmallIndependentProducerPage(idx)

    request.userAnswers.get(page).map {
      answerModel =>

        val declaration = ItemSmallIndependentProducerHelper.constructDeclarationPrefix(idx).dropRight(1)

        val answer = messages(s"itemSmallIndependentProducer.${answerModel.producerType}")

        val optSeedNumber = answerModel.producerId.map(messages("itemSmallIndependentProducer.cya.seedNumber", _))

        val content = Seq(
          Some(p()(Text(declaration).asHtml)),
          Some(p()(Text(answer).asHtml)),
          optSeedNumber.map(seedNumber => p()(Text(seedNumber).asHtml))
        ).flatten.mkString

        SummaryListRowViewModel(
          key = s"$page.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(content)),
          actions = Seq(ActionItemViewModel(
            href = routes.ItemSmallIndependentProducerController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
            content = "site.change",
            id = s"changeItemSmallIndependentProducer${idx.displayIndex}"
          ).withVisuallyHiddenText(messages(s"$page.change.hidden")))
        )
    }
  }
}
