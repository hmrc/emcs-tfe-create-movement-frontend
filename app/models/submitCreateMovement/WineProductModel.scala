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
import models.sections.items.{ItemWineGrowingZone, ItemWineProductCategory}
import pages.sections.items._
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, Reads, Writes, __}
import utils.{ItemHelper, JsonOptionFormatter, ModelConstructorHelpers}

case class WineProductModel(
                             wineProductCategory: ItemWineProductCategory,
                             wineGrowingZoneCode: Option[ItemWineGrowingZone],
                             thirdCountryOfOrigin: Option[String],
                             otherInformation: Option[String],
                             wineOperations: Option[Seq[WineOperations]]
                           )

object WineProductModel extends ModelConstructorHelpers with JsonOptionFormatter {

  def applyAtIdx(idx: Index)(implicit request: DataRequest[_]): Option[WineProductModel] = {

    if (ItemHelper.isWine(idx)(request.userAnswers)) {
      Some(
        WineProductModel(
          wineProductCategory = mandatoryPage(ItemWineProductCategoryPage(idx)),
          wineGrowingZoneCode = ItemWineGrowingZonePage(idx).value,
          thirdCountryOfOrigin = ItemWineOriginPage(idx).value.map(_.countryCode),
          otherInformation = ItemWineMoreInformationPage(idx).value.flatten,
          wineOperations = ItemWineOperationsChoicePage(idx).value.map(_.toSeq)
        )
      )
    } else {
      None
    }
  }

  implicit val reads: Reads[WineProductModel] = Json.reads
  implicit val writes: Writes[WineProductModel] = (
    (__ \ "wineProductCategory").write[ItemWineProductCategory] and
      (__ \ "wineGrowingZoneCode").writeNullable[ItemWineGrowingZone] and
      (__ \ "thirdCountryOfOrigin").writeNullable[String] and
      (__ \ "otherInformation").writeNullable[String] and
      (__ \ "wineOperations").writeNullable[Seq[WineOperations]](Writes.seq(WineOperations.submissionWrites))
    )(unlift(WineProductModel.unapply)
  )

  val auditWrites: Writes[WineProductModel] = (
    (__ \ "wineProductCategory").write[ItemWineProductCategory](Auditable.writes[ItemWineProductCategory]) and
      (__ \ "wineGrowingZoneCode").writeNullable[ItemWineGrowingZone](Auditable.writes[ItemWineGrowingZone]) and
      (__ \ "thirdCountryOfOrigin").writeNullable[String] and
      (__ \ "otherInformation").writeNullable[String] and
      (__ \ "wineOperations").writeNullable[Seq[WineOperations]](Writes.seq(WineOperations.auditWrites))
    )(unlift(WineProductModel.unapply)
  )
}
