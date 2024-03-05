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

package models.response

import play.api.libs.json._

case class SubmitCreateMovementResponse(receipt: String, downstreamService: String, submittedDraftId: String)

object SubmitCreateMovementResponse {
  implicit val reads: Reads[SubmitCreateMovementResponse] = for {
    submittedDraftId <- (__ \ "submittedDraftId").read[String]
    optMessage <- (__ \ "message").readNullable[String]
    optReceipt <- (__ \ "receipt").readNullable[String]
  } yield {
    if(optMessage.isDefined) {
      SubmitCreateMovementResponse(optMessage.get, "EIS", submittedDraftId)
    } else {
      SubmitCreateMovementResponse(optReceipt.get, "ChRIS", submittedDraftId)
    }
  }


  implicit val writes: OWrites[SubmitCreateMovementResponse] =
    (o: SubmitCreateMovementResponse) => Json.obj("receipt" -> o.receipt)
}
