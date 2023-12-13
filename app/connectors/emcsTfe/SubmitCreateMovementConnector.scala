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

import config.AppConfig
import models.requests.DataRequest
import models.response.{ErrorResponse, SubmitCreateMovementResponse}
import models.submitCreateMovement.SubmitCreateMovementModel
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitCreateMovementConnector @Inject()(val http: HttpClient,
                                              config: AppConfig) extends EmcsTfeHttpParser[SubmitCreateMovementResponse] {

  override implicit val reads: Reads[SubmitCreateMovementResponse] = SubmitCreateMovementResponse.fmt

  lazy val baseUrl: String = config.emcsTfeBaseUrl
  def submit(submitCreateMovementModel: SubmitCreateMovementModel)
            (implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, SubmitCreateMovementResponse]] =
    post(s"$baseUrl/create-movement/${request.ern}/${request.draftId}", submitCreateMovementModel)

}
