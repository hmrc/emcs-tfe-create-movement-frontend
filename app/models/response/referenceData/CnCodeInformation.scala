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

package models.response.referenceData

import models.UnitOfMeasure
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}

case class CnCodeInformation(cnCode: String,
                             cnCodeDescription: String,
                             exciseProductCode: String,
                             exciseProductCodeDescription: String,
                             unitOfMeasure: UnitOfMeasure)

object CnCodeInformation {
  val defaultCnCode: String = "10000000"

  implicit val reads: Reads[CnCodeInformation] = (
    (JsPath \ "cnCode").read[String] and
      (JsPath \ "cnCodeDescription").read[String] and
      (JsPath \ "exciseProductCode").read[String] and
      (JsPath \ "exciseProductCodeDescription").read[String] and
      (JsPath \ "unitOfMeasureCode").read[Int].map(UnitOfMeasure.apply)
    )(CnCodeInformation.apply _)

  implicit val writes: Writes[CnCodeInformation] = (
    (JsPath \ "cnCode").write[String] and
      (JsPath \ "cnCodeDescription").write[String] and
      (JsPath \ "exciseProductCode").write[String] and
      (JsPath \ "exciseProductCodeDescription").write[String] and
      (JsPath \ "unitOfMeasureCode").write[Int].contramap(UnitOfMeasure.unapply)
    )(unlift(CnCodeInformation.unapply))

  implicit val format: Format[CnCodeInformation] = Format(reads,writes)
}
