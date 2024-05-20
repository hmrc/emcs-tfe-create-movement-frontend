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

package connectors.emcsTfe

import config.AppConfig
import models.UserAnswers
import models.requests.DataRequest
import models.response.ErrorResponse
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteDraftMovementConnector @Inject()(val http: HttpClient,
                                             config: AppConfig) extends UserAnswersHttpParsers {

  //Unused as this connector deletes the draft, which returns no response body
  override implicit val reads: Reads[UserAnswers] = UserAnswers.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def deleteDraft()(implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    delete(s"$baseUrl/user-answers/create-movement/${request.ern}/${request.draftId}")
}
