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

import models.requests.DataRequest
import models.{GoodsType, Index}
import pages.sections.items._
import play.api.libs.json.{Json, OFormat}
import utils.{JsonOptionFormatter, ModelConstructorHelpers}

case class WineProductModel(
                             wineProductCategory: String,
                             wineGrowingZoneCode: Option[String],
                             thirdCountryOfOrigin: Option[String],
                             otherInformation: Option[String],
                             wineOperations: Option[Seq[String]]
                           )

object WineProductModel extends ModelConstructorHelpers with JsonOptionFormatter {

  private def wineProductCategory(idx: Index)(implicit request: DataRequest[_]): String = {
    /**
     * From tcl.xsd:
     * <!--=========================================-->
     * <!--===== Category of Wine Product =====-->
     * <!--=========================================-->
     * <xs:simpleType name="CategoryOfWineProduct">
     *   <xs:annotation>
     *     <xs:documentation>Category of Wine Product</xs:documentation>
     *   </xs:annotation>
     *   <xs:restriction base="xs:nonNegativeInteger">
     *     <xs:enumeration value="1">
     *       <xs:annotation>
     *         <xs:documentation>Wine without PDO/PGI</xs:documentation>
     *       </xs:annotation>
     *     </xs:enumeration>
     *     <xs:enumeration value="2">
     *       <xs:annotation>
     *         <xs:documentation>Varietal wine without PDO/PGI</xs:documentation>
     *       </xs:annotation>
     *     </xs:enumeration>
     *     <xs:enumeration value="3">
     *       <xs:annotation>
     *         <xs:documentation>Wine with PDO or PGI</xs:documentation>
     *       </xs:annotation>
     *     </xs:enumeration>
     *     <xs:enumeration value="4">
     *       <xs:annotation>
     *         <xs:documentation>Imported wine</xs:documentation>
     *       </xs:annotation>
     *     </xs:enumeration>
     *     <xs:enumeration value="5">
     *       <xs:annotation>
     *         <xs:documentation>Other</xs:documentation>
     *       </xs:annotation>
     *     </xs:enumeration>
     *   </xs:restriction>
     * </xs:simpleType>
     */

    ???
  }

  def apply(exciseProductCode: String, idx: Index)(implicit request: DataRequest[_]): Option[WineProductModel] = {

    if (GoodsType.apply(exciseProductCode) == GoodsType.Wine) {
      Some(
        WineProductModel(
          wineProductCategory = wineProductCategory(idx),
          wineGrowingZoneCode = request.userAnswers.get(ItemWineGrowingZonePage(idx)).map(_.toString),
          thirdCountryOfOrigin = request.userAnswers.get(ItemWineOriginPage(idx)).map(_.countryCode),
          otherInformation = request.userAnswers.get(ItemWineMoreInformationPage(idx)).flatten,
          wineOperations = request.userAnswers.get(ItemWineOperationsChoicePage(idx)).map(_.toSeq.map(_.code))
        )
      )
    } else {
      None
    }
  }

  implicit val fmt: OFormat[WineProductModel] = Json.format
}
