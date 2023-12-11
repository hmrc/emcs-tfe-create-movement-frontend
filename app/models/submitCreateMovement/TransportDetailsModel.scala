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

package models.submitCreateMovement

import play.api.libs.json.{Json, OFormat}

case class TransportDetailsModel(
    transportUnitCode: String,
    identityOfTransportUnits: Option[String],
    commercialSealIdentification: Option[String],
    complementaryInformation: Option[String],
    sealInformation: Option[String]
)

object TransportDetailsModel {

//  implicit val xmlReads: XmlReader[TransportDetailsModel] = (
//    (__ \\ "TransportUnitCode").read[String],
//    (__ \\ "IdentityOfTransportUnits").read[Option[String]],
//    (__ \\ "CommercialSealIdentification").read[Option[String]],
//    (__ \\ "ComplementaryInformation").read[Option[String]],
//    (__ \\ "SealInformation").read[Option[String]]
//  ).mapN(TransportDetailsModel.apply)

  implicit val fmt: OFormat[TransportDetailsModel] = Json.format
}
