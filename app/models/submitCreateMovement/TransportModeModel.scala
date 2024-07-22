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

import models.audit.Auditable
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, OFormat, OWrites, __}
import utils.ModelConstructorHelpers

case class TransportModeModel(
                               transportModeCode: HowMovementTransported,
                               complementaryInformation: Option[String]
                             )

object TransportModeModel extends ModelConstructorHelpers {

  def apply(implicit request: DataRequest[_]): TransportModeModel = TransportModeModel(
    transportModeCode = mandatoryPage(HowMovementTransportedPage),
    complementaryInformation = GiveInformationOtherTransportPage.value
  )

  implicit val fmt: OFormat[TransportModeModel] = Json.format

  val auditWrites: OWrites[TransportModeModel] = (
    (__ \ "transportModeCode").write[HowMovementTransported](Auditable.writes[HowMovementTransported]) and
      (__ \ "complementaryInformation").writeNullable[String]
    )(unlift(TransportModeModel.unapply))
}
