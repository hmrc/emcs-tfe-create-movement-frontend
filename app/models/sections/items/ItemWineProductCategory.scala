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

import models.audit.Auditable
import models.requests.DataRequest
import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait ItemWineProductCategory

object ItemWineProductCategory extends Enumerable.Implicits {

  case object EuWineWithoutPdoOrPgi extends WithName("1") with ItemWineProductCategory with Auditable {
    override val auditDescription: String = "EuWineWithoutPdoOrPgi"
  }

  case object EuVarietalWineWithoutPdoOrPgi extends WithName("2") with ItemWineProductCategory with Auditable {
    override val auditDescription: String = "EuVarietalWineWithoutPdoOrPgi"
  }

  case object EuWineWithPdoOrPgiOrGi extends WithName("3") with ItemWineProductCategory with Auditable {
    override val auditDescription: String = "EuWineWithPdoOrPgiOrGi"
  }

  case object ImportedWine extends WithName("4") with ItemWineProductCategory with Auditable {
    override val auditDescription: String = "ImportedWine"
  }

  case object Other extends WithName("5") with ItemWineProductCategory with Auditable {
    override val auditDescription: String = "Other"
  }

  private val northernIrelandDisplayValues: Seq[ItemWineProductCategory] = Seq(
    EuWineWithoutPdoOrPgi, EuVarietalWineWithoutPdoOrPgi, EuWineWithPdoOrPgiOrGi, ImportedWine, Other
  )

  private val greatBritainDisplayValues: Seq[ItemWineProductCategory] = Seq(
    ImportedWine, Other
  )

  val allValues: Seq[ItemWineProductCategory] = Seq(
    EuWineWithoutPdoOrPgi, EuVarietalWineWithoutPdoOrPgi, EuWineWithPdoOrPgiOrGi, ImportedWine, Other
  )

  def options(implicit request: DataRequest[_], messages: Messages): Seq[RadioItem] = {

    def items(opt: Seq[ItemWineProductCategory]): Seq[RadioItem] =
      opt.map {
        value =>
          RadioItem(
            content = Text(messages(s"itemWineProductCategory.${value.toString}")),
            value = Some(value.toString),
            id = Some(s"value_${value.toString}")
          )
      }

    if (request.isNorthernIrelandErn) items(northernIrelandDisplayValues) else items(greatBritainDisplayValues)
  }

  implicit val enumerable: Enumerable[ItemWineProductCategory] =
    Enumerable(allValues.map(v => v.toString -> v): _*)
}
