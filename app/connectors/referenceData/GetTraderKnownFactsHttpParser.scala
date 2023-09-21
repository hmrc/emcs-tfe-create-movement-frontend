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

package connectors.referenceData

import connectors.BaseConnectorUtils
import models.TraderKnownFacts
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait GetTraderKnownFactsHttpParser extends BaseConnectorUtils[TraderKnownFacts] {


  implicit val reads: Reads[TraderKnownFacts] = TraderKnownFacts.format
  def http: HttpClient

  implicit class GetTraderKnownFactsReads(exciseRegistrationId: String) extends HttpReads[Either[ErrorResponse, Option[TraderKnownFacts]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Option[TraderKnownFacts]] = {
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid) => Right(Some(valid))
            case None =>
              logger.warn(s"[read] Bad JSON response from emcs-tfe-reference-data")
              Left(JsonValidationError)
          }
        case NO_CONTENT =>
          logger.warn(s"[read] No trader known facts found for $exciseRegistrationId")
          Right(None)
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-reference-data: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def get(url: String, exciseRegistrationId: String)
         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Option[TraderKnownFacts]]] =
    http.GET[Either[ErrorResponse, Option[TraderKnownFacts]]](
      url = url,
      queryParams = Seq("exciseRegistrationId" -> exciseRegistrationId)
    )(GetTraderKnownFactsReads(exciseRegistrationId), hc, ec)
}
