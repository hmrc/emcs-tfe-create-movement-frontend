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

package models

import play.api.libs.json.{Format, Json}

case class ExciseProductCode(code: String,
                             description: String,
                             category: String,
                             categoryDescription: String) extends SelectOptionModel {
  val displayName = s"$code: $description"
}

object ExciseProductCode {
  val epcsWithNoCnCodes: Seq[String] = Seq("S500")
  val epcsOnlyOneCnCode: Seq[String] = Seq("T300", "S400", "S600", "E600", "E800", "E910")

  implicit val format: Format[ExciseProductCode] = Json.format[ExciseProductCode]
}