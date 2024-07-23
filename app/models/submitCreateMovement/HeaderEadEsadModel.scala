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
import models.response.MissingMandatoryPage
import models.sections.info.movementScenario.DestinationType
import models.sections.transportArranger.TransportArranger
import pages.sections.journeyType.{JourneyTimeDaysPage, JourneyTimeHoursPage}
import pages.sections.transportArranger.TransportArrangerPage
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, OFormat, OWrites, __}
import utils.ModelConstructorHelpers

case class HeaderEadEsadModel(
                               destinationType: DestinationType,
                               journeyTime: String,
                               transportArrangement: TransportArranger
                             )

object HeaderEadEsadModel extends ModelConstructorHelpers {

  def apply(destinationType: DestinationType)(implicit request: DataRequest[_]): HeaderEadEsadModel = {
    val journeyTime: String = (JourneyTimeHoursPage.value, JourneyTimeDaysPage.value) match {
      case (Some(hours), _) => s"$hours hours"
      case (_, Some(days)) => s"$days days"
      case _ =>
        logger.error("Missing mandatory UserAnswer for journeyTime")
        throw MissingMandatoryPage("Missing mandatory UserAnswer for journeyTime")
    }

    HeaderEadEsadModel(
      destinationType = destinationType,
      journeyTime = journeyTime,
      transportArrangement = mandatoryPage(TransportArrangerPage)
    )
  }

  implicit val fmt: OFormat[HeaderEadEsadModel] = Json.format

  val auditWrites: OWrites[HeaderEadEsadModel] = (
    (__ \ "destinationType").write[DestinationType](Auditable.writes[DestinationType]) and
    (__ \ "journeyTime").write[String] and
      (__ \ "transportArrangement").write[TransportArranger](Auditable.writes[TransportArranger])
    )(unlift(HeaderEadEsadModel.unapply))
}
