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

sealed trait ItemGeographicalIndicationType

case object ItemGeographicalIndicationType extends Enumerable.Implicits {

  case object ProtectedDesignationOfOrigin extends WithName("PDO") with ItemGeographicalIndicationType

  case object ProtectedGeographicalIndication extends WithName("PGI") with ItemGeographicalIndicationType

  case object NoGeographicalIndication extends WithName("None") with ItemGeographicalIndicationType

  val values: Seq[ItemGeographicalIndicationType] = Seq(
    ProtectedDesignationOfOrigin, ProtectedGeographicalIndication, NoGeographicalIndication
  )

  implicit val enumerable: Enumerable[ItemGeographicalIndicationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
