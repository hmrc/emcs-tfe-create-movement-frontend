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
import pages.sections.info.DeferredMovementPage
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, OFormat, OWrites, __}
import utils.ModelConstructorHelpers

case class AttributesModel(
                            submissionMessageType: SubmissionMessageType,
                            deferredSubmissionFlag: Option[Boolean]
                          )
object AttributesModel extends ModelConstructorHelpers {
  implicit val fmt: OFormat[AttributesModel] = Json.format

  val auditWrites: OWrites[AttributesModel] = (
    (__ \ "submissionMessageType").write[SubmissionMessageType](Auditable.writes[SubmissionMessageType]) and
      (__ \ "deferredSubmissionFlag").writeNullable[Boolean]
  )(unlift(AttributesModel.unapply))

  private def deriveSubmissionMessageType(ern: String): SubmissionMessageType =
    if(ern.startsWith("XIP")) SubmissionMessageType.DutyPaidB2B else SubmissionMessageType.Standard

  def apply(implicit request: DataRequest[_]): AttributesModel = AttributesModel(
    submissionMessageType = deriveSubmissionMessageType(request.ern),
    deferredSubmissionFlag = DeferredMovementPage().value
  )
}
