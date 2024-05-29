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

import models.Index
import models.audit.Auditable
import models.requests.DataRequest
import models.sections.documents.DocumentType
import pages.sections.documents._
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, Reads, Writes, __}
import queries.DocumentsCount
import utils.ModelConstructorHelpers

case class DocumentCertificateModel(
                                     documentType: Option[DocumentType],
                                     documentReference: Option[String],
                                     documentDescription: Option[String],
                                     referenceOfDocument: Option[String]
                                   )

object DocumentCertificateModel extends ModelConstructorHelpers {

  def applyFromRequest(implicit request: DataRequest[_]): Option[Seq[DocumentCertificateModel]] = {
    val thereAnyDocumentCertificates = mandatoryPage(DocumentsCertificatesPage)

    if (!thereAnyDocumentCertificates) None else {
      request.userAnswers.get(DocumentsCount) match {
        case Some(0) | None => None
        case Some(value) =>
          Some((0 until value)
            .map(Index(_))
            .map {
              idx =>
                DocumentCertificateModel(
                  documentType = Some(mandatoryPage(DocumentTypePage(idx))),
                  documentReference = Some(mandatoryPage(DocumentReferencePage(idx))),
                  documentDescription = None,
                  referenceOfDocument = None
                )
            })
      }
    }
  }

  implicit val reads: Reads[DocumentCertificateModel] = Json.reads
  implicit val writes: Writes[DocumentCertificateModel] = (
    (__ \ "documentType").writeNullable[DocumentType](DocumentType.submissionWrites) and
      (__ \ "documentReference").writeNullable[String] and
      (__ \ "documentDescription").writeNullable[String] and
      (__ \ "referenceOfDocument").writeNullable[String]
    )(unlift(DocumentCertificateModel.unapply)
  )

  val auditWrites: Writes[DocumentCertificateModel] = (
    (__ \ "documentType").writeNullable[DocumentType](DocumentType.auditWrites) and
      (__ \ "documentReference").writeNullable[String] and
      (__ \ "documentDescription").writeNullable[String] and
      (__ \ "referenceOfDocument").writeNullable[String]
    )(unlift(DocumentCertificateModel.unapply)
  )

}
