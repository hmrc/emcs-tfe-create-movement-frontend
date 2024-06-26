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
import models.{Enumerable, WithName}

sealed trait SubmissionMessageType

object SubmissionMessageType extends Enumerable.Implicits {

  case object Standard extends WithName("1") with SubmissionMessageType with Auditable {
    override val auditDescription: String = "Standard"
  }

  case object DutyPaidB2B extends WithName("3") with SubmissionMessageType with Auditable {
    override val auditDescription: String = "DutyPaid"
  }

  val values: Seq[SubmissionMessageType] = Seq(Standard, DutyPaidB2B)

  implicit val enumerable: Enumerable[SubmissionMessageType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
