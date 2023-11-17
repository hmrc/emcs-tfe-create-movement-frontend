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

package models.sections.items

import models.{Enumerable, WithName}
import play.api.libs.json.{JsString, Writes}

sealed trait ItemBulkPackagingCode

case object ItemBulkPackagingCode extends Enumerable.Implicits {

  case object BulkGas extends WithName("VG") with ItemBulkPackagingCode

  case object BulkLiquefiedGas extends WithName("VQ") with ItemBulkPackagingCode

  case object BulkLiquid extends WithName("VL") with ItemBulkPackagingCode

  case object BulkSolidPowders extends WithName("VY") with ItemBulkPackagingCode

  case object BulkSolidGrains extends WithName("VR") with ItemBulkPackagingCode

  case object BulkSolidNodules extends WithName("VO") with ItemBulkPackagingCode

  case object Unpacked extends WithName("NE") with ItemBulkPackagingCode

  val values: Seq[ItemBulkPackagingCode] = Seq(
    BulkGas, BulkLiquefiedGas, BulkLiquid, BulkSolidPowders, BulkSolidGrains, BulkSolidNodules, Unpacked
  )

  implicit val enumerable: Enumerable[ItemBulkPackagingCode] =
    Enumerable(values.map(v => v.toString -> v): _*)

  implicit val writes: Writes[ItemBulkPackagingCode] = (o: ItemBulkPackagingCode) => JsString(o.toString)

}

