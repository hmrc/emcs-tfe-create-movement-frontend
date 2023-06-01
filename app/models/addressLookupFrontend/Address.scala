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

package models.addressLookupFrontend

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Address(lines: Seq[String],
                   postcode: Option[String],
                   country: Option[Country],
                   auditRef: Option[String] = None)

object Address {

  implicit val reads: Reads[Address] = (
    (__ \\ "lines").read[Seq[String]] and
      (__ \\ "postcode").readNullable[String] and
      (__ \\ "country").readNullable[Country] and
      (__ \\ "auditRef").readNullable[String]
    )(Address.apply _)

  implicit val writes: OWrites[Address] = (
      (__ \ "lines").write[Seq[String]] and
        (__ \ "postcode").writeNullable[String] and
        (__ \ "country").writeNullable[Country] and
        (__ \ "auditRef").writeNullable[String]
      )(unlift(Address.unapply))
}
