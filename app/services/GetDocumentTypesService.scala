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

package services

import connectors.referenceData.GetDocumentTypesConnector
import models.response.DocumentTypesException
import models.sections.documents.DocumentType
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetDocumentTypesService @Inject()(connector: GetDocumentTypesConnector)
                                       (implicit ec: ExecutionContext) {

  def getDocumentTypes()(implicit hc: HeaderCarrier): Future[Seq[DocumentType]] = {
    connector.getDocumentTypes().map {
      case Left(_) => throw DocumentTypesException("No document types retrieved")
      case Right(documentTypes) =>
        val (numeric, alphaNumeric) = documentTypes.partition(_.code.toIntOption.exists(_ >= 0))
        val sortedNumeric = numeric.sortBy(doc => (doc.code.toInt, doc.description))
        val sortedAlphaNumeric = alphaNumeric.sortBy(doc => (doc.code, doc.description))
        sortedNumeric ++ sortedAlphaNumeric
    }
  }
}
