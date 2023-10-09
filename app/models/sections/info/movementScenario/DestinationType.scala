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

package models.sections.info.movementScenario

import models.{Enumerable, WithName}

sealed trait DestinationType {
  val stringValue: String
}

object DestinationType extends Enumerable.Implicits {
  case object TaxWarehouse extends WithName("1") with DestinationType {
    override val stringValue: String = "tax warehouse"
  }

  case object RegisteredConsignee extends WithName("2") with DestinationType {
    override val stringValue: String = "registered consignee"
  }

  case object TemporaryRegisteredConsignee extends WithName("3") with DestinationType {
    override val stringValue: String = "temporary registered consignee"
  }

  case object DirectDelivery extends WithName("4") with DestinationType {
    override val stringValue: String = "direct delivery"
  }

  case object ExemptedOrganisations extends WithName("5") with DestinationType {
    override val stringValue: String = "exempted organisations"
  }

  //Both to be sent as `Export - 6` to backend. Differentiated for Frontend Content
  case object ExportWithCustomsLodgedInEU extends WithName("6EU") with DestinationType {
    override val stringValue: String = "export eu"
  }
  case object ExportWithCustomsLodgedInGB extends WithName("6GB") with DestinationType {
    override val stringValue: String = "export gb"
  }

  case object UnknownDestination extends WithName("8") with DestinationType {
    override val stringValue: String = "unknown destination"
  }

  case object CertifiedConsignee extends WithName("9") with DestinationType {
    override val stringValue: String = "certified consignee"
  }

  case object TemporaryCertifiedConsignee extends WithName("10") with DestinationType {
    override val stringValue: String = "temporary certified consignee"
  }

  case object ReturnToThePlaceOfDispatchOfTheConsignor extends WithName("11") with DestinationType {
    override val stringValue: String = "return to the place of dispatch of the consignor"
  }

  val values: Seq[DestinationType] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    DirectDelivery,
    ExemptedOrganisation,
    Export,
    UnknownDestination,
    CertifiedConsignee,
    TemporaryCertifiedConsignee,
    ReturnToThePlaceOfDispatchOfTheConsignor
  )

  implicit val enumerable: Enumerable[DestinationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}