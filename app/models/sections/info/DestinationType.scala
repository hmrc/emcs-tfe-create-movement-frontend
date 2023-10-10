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

package models.sections.info

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import play.api.libs.json.{JsString, Writes}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait DestinationType

object DestinationType extends Enumerable.Implicits {

  case object TaxWarehouse extends WithName("1") with DestinationType

  case object RegisteredConsignee extends WithName("2") with DestinationType

  case object TemporaryRegisteredConsignee extends WithName("3") with DestinationType

  case object DirectDelivery extends WithName("4") with DestinationType

  case object ExemptedOrganisations extends WithName("5") with DestinationType

  //Both to be sent as `Export - 6` to backend. Differentiated for Frontend Content
  case object ExportWithCustomsLodgedInEU extends WithName("6EU") with DestinationType
  case object ExportWithCustomsLodgedInGB extends WithName("6GB") with DestinationType

  case object UnknownDestination extends WithName("8") with DestinationType

  case object CertifiedConsignee extends WithName("9") with DestinationType

  case object TemporaryCertifiedConsignee extends WithName("10") with DestinationType

  case object ReturnToThePlaceOfDispatchOfTheConsignor extends WithName("11") with DestinationType

  val values: Seq[DestinationType] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    DirectDelivery,
    ExemptedOrganisations,
    ExportWithCustomsLodgedInEU,
    ExportWithCustomsLodgedInGB,
    UnknownDestination,
    CertifiedConsignee,
    TemporaryCertifiedConsignee,
    ReturnToThePlaceOfDispatchOfTheConsignor
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, _) =>
      RadioItem(
        content = Text(messages(s"destinationType.${value.toString}")),
        value = Some(value.toString),
        id = Some(s"value_${value.toString}")
      )
  }

  //TODO: To be used when submitting to Backend
  val submissionWrites: Writes[DestinationType] = {
    Writes {
      case ExportWithCustomsLodgedInEU | ExportWithCustomsLodgedInGB => JsString("6")
      case destination => JsString(destination.toString)
    }
  }

  implicit val enumerable: Enumerable[DestinationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
