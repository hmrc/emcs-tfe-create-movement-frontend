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
import models.sections.guarantor.GuarantorArranger
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import play.api.libs.json.{Json, OFormat, OWrites, __}
import utils.ModelConstructorHelpers
import models.sections.info.movementScenario.MovementScenario
import pages.sections.info.DestinationTypePage
import models.sections.info.movementScenario.MovementType
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}

case class MovementGuaranteeModel(
                                   guarantorTypeCode: GuarantorArranger,
                                   guarantorTrader: Option[Seq[TraderModel]]
                                 )

object MovementGuaranteeModel extends ModelConstructorHelpers {

  def apply(implicit request: DataRequest[_]): MovementGuaranteeModel = {
    val guarantorRequired: Boolean = request.userAnswers.get(GuarantorRequiredPage) match {
      case Some(value) => value
      case None => GuarantorRequiredPage.guarantorAlwaysRequired() || GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU()
    }

    if (!guarantorRequired) {
      val movementScenario: MovementScenario = mandatoryPage(DestinationTypePage)
      
      MovementGuaranteeModel(
        guarantorTypeCode = if(movementScenario.movementType == MovementType.UkToEu) GuarantorArranger.NoGuarantorRequiredUkToEu else GuarantorArranger.NoGuarantorRequired,
        guarantorTrader = None
      )
    } else {
      val guarantorArranger: GuarantorArranger = mandatoryPage(GuarantorArrangerPage)
      MovementGuaranteeModel(
        guarantorTypeCode = guarantorArranger,
        guarantorTrader = TraderModel.applyGuarantor(guarantorArranger).flatMap(trader => Some(Seq(trader)))
      )
    }

  }

  implicit val fmt: OFormat[MovementGuaranteeModel] = Json.format

  val auditWrites: OWrites[MovementGuaranteeModel] = (
    (__ \ "guarantorTypeCode").write[GuarantorArranger](Auditable.writes[GuarantorArranger]) and
      (__ \ "guarantorTrader").writeNullable[Seq[TraderModel]]
    )(unlift(MovementGuaranteeModel.unapply))
}
