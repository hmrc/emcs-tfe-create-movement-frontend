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
import models.CountryModel
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.JsResultException
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

trait GetMemberStatesConnector {
  def baseUrl: String

  def getMemberStates()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, Seq[CountryModel]]]
}

@Singleton
class GetMemberStatesConnectorImpl @Inject()(val http: HttpClient,
                                         config: AppConfig) extends GetMemberStatesHttpParser with GetMemberStatesConnector {

  def baseUrl: String = config.referenceDataBaseUrl

  def getMemberStates()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, Seq[CountryModel]]] =
    get(baseUrl + "/oracle/member-states")
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getMemberStates] Bad JSON response from emcs-tfe-reference-data: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getMemberStates] Unexpected error from reference-data: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
}
