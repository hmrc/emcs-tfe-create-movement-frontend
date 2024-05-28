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

package models.submitCreateMovement

import models.Index
import models.audit.Auditable
import models.requests.DataRequest
import models.response.referenceData.WineOperations
import models.sections.items.ItemWineProductCategory.ImportedWine
import models.sections.items.{ItemGeographicalIndicationType, ItemWineGrowingZone}
import pages.sections.items._
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, Reads, Writes, __}
import utils.{CommodityCodeHelper, JsonOptionFormatter, ModelConstructorHelpers}

case class WineProductModel(
                             wineProductCategory: ItemWineCategory,
                             wineGrowingZoneCode: Option[ItemWineGrowingZone],
                             thirdCountryOfOrigin: Option[String],
                             otherInformation: Option[String],
                             wineOperations: Option[Seq[WineOperations]]
                           )

object WineProductModel extends ModelConstructorHelpers with JsonOptionFormatter {

  private[submitCreateMovement] def wineProductCategory(idx: Index)(implicit request: DataRequest[_]): ItemWineCategory = {

    if (request.userAnswers.get(ItemWineProductCategoryPage(idx)).contains(ImportedWine)) {
      // if imported from outside EU
      ItemWineCategory.ImportedWine
    } else {
      // imported from inside EU
      val geographicalIndicationChoice: ItemGeographicalIndicationType = mandatoryPage(ItemDesignationOfOriginPage(idx)).geographicalIndication

      geographicalIndicationChoice match {
        case ItemGeographicalIndicationType.NoGeographicalIndication =>
          // if no GI
          val commodityCode = mandatoryPage(ItemCommodityCodePage(idx))
          if (ItemWineCategory.varietalWines.contains(commodityCode)) {
            ItemWineCategory.EuVarietalWineWithoutPdoOrPgi
          } else {
            ItemWineCategory.EuWineWithoutPdoOrPgi
          }

        case _ =>
          // if has PDO, PGI, or GI (umbrella term for PDO/PGI)
          ItemWineCategory.EuWineWithPdoOrPgiOrGi
      }
    }
  }

  def applyAtIdx(idx: Index)(implicit request: DataRequest[_]): Option[WineProductModel] = {

    if (request.userAnswers.get(ItemCommodityCodePage(idx)).exists(CommodityCodeHelper.isWineCommodityCode)) {
      Some(
        WineProductModel(
          wineProductCategory = wineProductCategory(idx),
          wineGrowingZoneCode = request.userAnswers.get(ItemWineGrowingZonePage(idx)),
          thirdCountryOfOrigin = request.userAnswers.get(ItemWineOriginPage(idx)).map(_.countryCode),
          otherInformation = request.userAnswers.get(ItemWineMoreInformationPage(idx)).flatten,
          wineOperations = request.userAnswers.get(ItemWineOperationsChoicePage(idx)).map(_.toSeq)
        )
      )
    } else {
      None
    }
  }

  implicit val reads: Reads[WineProductModel] = Json.reads
  implicit val writes: Writes[WineProductModel] = (
    (__ \ "wineProductCategory").write[ItemWineCategory] and
      (__ \ "wineGrowingZoneCode").writeNullable[ItemWineGrowingZone] and
      (__ \ "thirdCountryOfOrigin").writeNullable[String] and
      (__ \ "otherInformation").writeNullable[String] and
      (__ \ "wineOperations").writeNullable[Seq[WineOperations]](Writes.seq(WineOperations.submissionWrites))
    )(unlift(WineProductModel.unapply)
  )

  val auditWrites: Writes[WineProductModel] = (
    (__ \ "wineProductCategory").write[ItemWineCategory](Auditable.writes[ItemWineCategory]) and
      (__ \ "wineGrowingZoneCode").writeNullable[ItemWineGrowingZone](Auditable.writes[ItemWineGrowingZone]) and
      (__ \ "thirdCountryOfOrigin").writeNullable[String] and
      (__ \ "otherInformation").writeNullable[String] and
      (__ \ "wineOperations").writeNullable[Seq[WineOperations]](Writes.seq(WineOperations.auditWrites))
    )(unlift(WineProductModel.unapply)
  )
}
