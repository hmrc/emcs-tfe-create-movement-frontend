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
import models.GoodsTypeModel.GoodsType
import models.requests.DataRequest
import models.{Index, NormalMode, UserAnswers}
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemCommodityCodeSummary {
  def row(idx: Index, goodsType: GoodsType, userAnswers: UserAnswers)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    request.userAnswers.get(ItemCommodityCodePage(idx)).map { value =>
      SummaryListRowViewModel(
        key = messages("itemCommodityCode.checkYourAnswersLabel", goodsType.toSingularOutput()),
        value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
          Html(value.cnCode + "<br>"),
          Html(value.cnCodeDescription))))),
        actions =
          userAnswers.get(ItemExciseProductCodePage(idx)).map(_.code) match {
            case Some("S500" | "T300" | "S400" | "E600" | "E800" | "E910") =>
              Seq.empty
            case _ =>
              Seq(
                ActionItemViewModel(
                  content = "site.change",
                  routes.ItemCommodityCodeController.onPageLoad(request.userAnswers.ern, request.userAnswers.draftId, idx, NormalMode).url,
                  id = s"changeItemCommodityCode${idx.displayIndex}"
                )
                  .withVisuallyHiddenText(messages("itemCommodityCode.change.hidden"))
              )
          }
      )
    }
  }
}