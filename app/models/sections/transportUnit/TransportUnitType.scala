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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem


sealed trait TransportUnitType{
  val stringValue: String
}

object TransportUnitType extends Enumerable.Implicits {

  case object Container extends WithName("1") with TransportUnitType {
    val stringValue = "container"
  }

  case object Vehicle extends WithName("2") with TransportUnitType {
    val stringValue = "vehicle"
  }

  case object Trailer extends WithName("3") with TransportUnitType {
    val stringValue = "trailer"
  }

  case object Tractor extends WithName("4") with TransportUnitType {
    val stringValue = "tractor"
  }

  case object FixedTransport extends WithName("5") with TransportUnitType {
    val stringValue = "fixed transport installation"
  }

  val values: Seq[TransportUnitType] = Seq(
    Container, FixedTransport, Tractor, Trailer, Vehicle
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, _) =>
      RadioItem(
        content = Text(messages(s"transportUnitType.${value.toString}")),
        value = Some(value.toString),
        id = Some(s"value_${value.toString}")
      )
  }

  implicit val enumerable: Enumerable[TransportUnitType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
