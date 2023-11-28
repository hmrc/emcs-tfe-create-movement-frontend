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

import config.AppConfig
import models.response.referenceData.WineOperations
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.JsResultException
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait GetWineOperationsConnector {
  def baseUrl: String

  def getWineOperations()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, Seq[WineOperations]]]
}

class GetWineOperationsConnectorImpl @Inject()(val http: HttpClient,
                                               config: AppConfig) extends GetWineOperationsHttpParser with GetWineOperationsConnector {

  override def baseUrl: String = config.referenceDataBaseUrl

  override def getWineOperations()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext):
  Future[Either[ErrorResponse, Seq[WineOperations]]] = {
    get(s"$baseUrl/oracle/wine-operations")
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getWineOperations] Bad JSON response from emcs-tfe-reference-data: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getWineOperations] Unexpected error from cn-codes: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }

}
