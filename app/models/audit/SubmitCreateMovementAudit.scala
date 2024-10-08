/*
 * Copyright 2024 HM Revenue & Customs
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

package models.audit

import models.response.{ErrorResponse, SubmitCreateMovementResponse}
import models.submitCreateMovement.SubmitCreateMovementModel
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

case class SubmitCreateMovementAudit(
                                      ern: String,
                                      templateName: Option[String],
                                      templateId: Option[String],
                                      receiptDate: String,
                                      submissionRequest: SubmitCreateMovementModel,
                                      submissionResponse: Either[ErrorResponse, SubmitCreateMovementResponse]
                                    ) extends AuditModel {

  override val auditType: String = "SubmitDraftMovement"

  override val detail: JsValue = JsObject(fields =
    Seq(
      Some("exciseRegistrationNumber" -> JsString(ern)),
      templateName.map(name => "templateName" -> JsString(name)),
      templateId.map(id => "templateId" -> JsString(id))
    ).flatten
  ).deepMerge(Json.toJsObject(submissionRequest)(SubmitCreateMovementModel.auditWrites)) ++ {
    submissionResponse match {
      case Right(success) =>
        Json.obj(fields =
          "status" -> "success",
          "receipt" -> success.receipt,
          "receiptDate" -> receiptDate,
          "responseCode" -> OK
        )
      case Left(failedMessage) =>
        Json.obj(fields =
          "status" -> "failed",
          "failedMessage" -> failedMessage.message
        ).deepMerge(
          failedMessage.statusCode.fold(Json.obj())(statusCode => Json.obj("responseCode" -> statusCode))
        )
    }
  }
}
