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

package models.sections.transportUnit

import models.audit.Auditable
import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewmodels.govuk.all.HintViewModel


sealed trait TransportUnitType

object TransportUnitType extends Enumerable.Implicits {

  case object Container extends WithName("1") with TransportUnitType with Auditable {
    override val auditDescription: String = "Container"
  }

  case object Vehicle extends WithName("2") with TransportUnitType with Auditable {
    override val auditDescription: String = "Vehicle"
  }

  case object Trailer extends WithName("3") with TransportUnitType with Auditable {
    override val auditDescription: String = "Trailer"
  }

  case object Tractor extends WithName("4") with TransportUnitType with Auditable {
    override val auditDescription: String = "Tractor"
  }

  case object FixedTransport extends WithName("5") with TransportUnitType with Auditable {
    override val auditDescription: String = "FixedTransport"
  }

  val values: Seq[TransportUnitType] = Seq(
    Container, FixedTransport, Tractor, Trailer, Vehicle
  )

  val hints: Seq[TransportUnitType] = Seq(
    Tractor, Vehicle
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      content = Text(messages(s"transportUnitType.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_${value.toString}"),
      hint = {if (hints.contains(value)) Some(HintViewModel(Text(messages(s"transportUnitType.${value.toString}.hint"))))else None}
    )
  }

  implicit val enumerable: Enumerable[TransportUnitType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
