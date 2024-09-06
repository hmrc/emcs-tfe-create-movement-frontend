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

package connectors.emcsTfe

import connectors.BaseConnectorUtils
import models.response.templates.MovementTemplate
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamDraftSubmissionResponseError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait GetListOfTemplatesHttpParser extends BaseConnectorUtils[Seq[MovementTemplate]] {

  override implicit val reads: Reads[Seq[MovementTemplate]] = Reads.seq[MovementTemplate]

  implicit object GetListOfTemplatesReads extends HttpReads[Either[ErrorResponse, Seq[MovementTemplate]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Seq[MovementTemplate]] = {
      response.status match {
        case OK => response.validateJson match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"[read] Bad JSON response from emcs-tfe")
            Left(JsonValidationError)
        }
        case NO_CONTENT => Right(Seq())
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
