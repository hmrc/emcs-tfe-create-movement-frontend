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
import pages.sections.items._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import queries.ItemsPackagingCount
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

import javax.inject.Inject

class ItemPackagingSummary @Inject()(
                                      tag: views.html.components.tag,
                                      details: views.html.components.details,
                                      list: list
                                    ) {

  def row(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    ItemBulkPackagingChoicePage(itemIdx).value.flatMap {
      case true =>
        constructBulkPackagingSummary(itemIdx)
      case _ =>
        getPackagesForItem(itemIdx) match {
          case Nil => None
          case packages =>
            Some(SummaryListRowViewModel(
              key = "itemsAddToList.packagesCyaLabel",
              value = ValueViewModel(HtmlContent(list(constructPackagingValues(itemIdx, packages))))
            ))
        }
    }

  private[items] def getPackagesForItem(itemIdx: Index)(implicit request: DataRequest[_]): Seq[ItemPackagingModel] =
    request.userAnswers.getCount(ItemsPackagingCount(itemIdx)).fold[Seq[ItemPackagingModel]](Seq.empty) { count =>
      (0 until count).map(Index(_)).map { packageIdx =>
        (ItemSelectPackagingPage(itemIdx, packageIdx).value,
          ItemPackagingQuantityPage(itemIdx, packageIdx).value,
          ItemPackagingShippingMarksPage(itemIdx, packageIdx).value)
      }.collect {
        case (Some(packaging), quantity, shippingMark) => ItemPackagingModel(packaging, quantity, shippingMark)
      }
    }

  private[items] def constructPackagingValues(itemIdx: Index, packages: Seq[ItemPackagingModel])
                                             (implicit request: DataRequest[_], messages: Messages): Seq[Html] = {
    if (packages.exists(_.quantity.isEmpty)) {
      // if any quantities are missing, show a single incomplete tag for the whole packages section
      Seq(HtmlFormat.fill(
        Seq(incompleteTag(setMarginLeft = false))
      ))
    } else {
      //Note: `None` on quantity is guarded by the above if statement check - so pattern match is exhaustive
      packages.zipWithIndex.map {
        case (ItemPackagingModel(itemPackaging, Some(quantity), Some(shippingMarks)), packageIdx) =>
          HtmlFormat.fill(
            Seq(Html(messages("itemsAddToList.packagesCyaValueShippingMark", quantity, itemPackaging.description, truncateShippingMark(shippingMarks)))) ++
              (if (shippingMarks.length <= 30) Seq() else Seq(details("itemsAddToList.packagesCyaValueShippingMarkDetails", "govuk-!-margin-top-2 govuk-!-margin-bottom-2")(Html(shippingMarks)))) ++
              (if (ItemsPackagingSectionItems(itemIdx, Index(packageIdx)).isCompleted) Seq() else Seq(incompleteTag()))
          )
        case (ItemPackagingModel(itemPackaging, Some(quantity), None), packageIdx) =>
          HtmlFormat.fill(
            Html(messages("itemsAddToList.packagesCyaValue", quantity, itemPackaging.description)) +:
              (if (ItemsPackagingSectionItems(itemIdx, Index(packageIdx)).isCompleted) Seq() else Seq(incompleteTag()))
          )
      }
    }
  }

  private[items] def constructBulkPackagingSummary(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    ItemBulkPackagingSelectPage(itemIdx).value.map { bulkType =>
      SummaryListRowViewModel(
        key = "itemsAddToList.packagesCyaLabel",
        value = ValueViewModel(HtmlContent(
          HtmlFormat.fill(Seq(
            Some(Html(bulkType.description)),
            if (ItemsSectionItem(itemIdx).bulkPackagingPagesComplete) None else Some(incompleteTag())
          ).flatten)
        ))
      )
    }
  }

  private def incompleteTag(setMarginLeft: Boolean = true)(implicit messages: Messages): Html = tag(
    message = messages("taskListStatus.incomplete"),
    colour = "red",
    extraClasses = ("float-none" +: (if (setMarginLeft) Seq("govuk-!-margin-left-2") else Seq())).mkString(" ")
  )

  private[items] val truncateShippingMark: String => String = {
    case mark if mark.length <= 30 => mark
    case mark => mark.take(30) + "..."
  }
}
