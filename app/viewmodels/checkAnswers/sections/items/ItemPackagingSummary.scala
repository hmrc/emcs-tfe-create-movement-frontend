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

import models.Index
import models.requests.DataRequest
import models.response.referenceData.ItemPackaging
import pages.sections.items._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import queries.ItemsPackagingCount
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.Logging
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import javax.inject.Inject

class ItemPackagingSummary @Inject()(tag: views.html.components.tag) extends Logging {

  def row(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ItemBulkPackagingChoicePage(itemIdx)).flatMap {
      case true =>
        constructBulkPackagingSummary(itemIdx)
      case _ =>
        getPackagesForItem(itemIdx) match {
          case Nil => None
          case packages =>
            Some(SummaryListRowViewModel(
              key = "itemsAddToList.packagesCyaLabel",
              value = ValueViewModel(HtmlContent(constructPackagingValues(itemIdx, packages).mkString("<br>")))
            ))
        }
    }

  private def getPackagesForItem(itemIdx: Index)(implicit request: DataRequest[_]): Seq[(ItemPackaging, Option[String])] =
    request.userAnswers.get(ItemsPackagingCount(itemIdx)).fold(Seq.empty[(ItemPackaging, Option[String])]) { count =>
      (0 until count).map(Index(_)).map { packageIdx =>
        request.userAnswers.get(ItemSelectPackagingPage(itemIdx, packageIdx)) ->
          request.userAnswers.get(ItemPackagingQuantityPage(itemIdx, packageIdx))
      }.collect {
        case (Some(epc), quantity) => epc -> quantity
      }
    }

  private def constructPackagingValues(itemIdx: Index,
                                       packages: Seq[(ItemPackaging, Option[String])])(implicit request: DataRequest[_], messages: Messages): Seq[Html] =
    packages.zipWithIndex.map {
      case ((itemPackaging, quantity), packageIdx) =>
        HtmlFormat.fill(Seq(
          Some(Html(s"${quantity.fold("")(_ + " ")}${itemPackaging.description}")),
          if (ItemsPackagingSectionItems(itemIdx, Index(packageIdx)).isCompleted) None else Some(incompleteTag)
        ).flatten)
    }

  private def constructBulkPackagingSummary(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ItemBulkPackagingSelectPage(itemIdx)).map { bulkType =>
      SummaryListRowViewModel(
        key = "itemsAddToList.packagesCyaLabel",
        value = ValueViewModel(HtmlContent(
          HtmlFormat.fill(Seq(
            Some(Html(bulkType.description)),
            if (ItemsSectionItem(itemIdx).bulkPackagingPagesComplete) None else Some(incompleteTag)
          ).flatten)
        ))
      )
    }

  private def incompleteTag(implicit messages: Messages): Html = tag(
    message = messages("taskListStatus.incomplete"),
    colour = "red",
    extraClasses = "float-none govuk-!-margin-left-2"
  )
}
