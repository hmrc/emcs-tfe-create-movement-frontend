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
import models.response.ErrorResponse
import models.response.templates.{MovementTemplate, MovementTemplates}
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovementTemplatesConnector @Inject()(val http: HttpClientV2,
                                           config: AppConfig) extends GetListOfTemplatesHttpParser with SaveTemplateHttpParser {

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def getList(ern: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, MovementTemplates]] =
    withExceptionRecovery("getList") {
      http
        .get(url"$baseUrl/templates/$ern")
        .execute[Either[ErrorResponse, MovementTemplates]]
    }(ec, logger)

  def saveTemplate(movementTemplate: MovementTemplate)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    withExceptionRecovery("saveTemplate") {
      http
        .put(url"$baseUrl/template/${movementTemplate.ern}/${movementTemplate.templateId}")
        .withBody(Json.toJson(movementTemplate))
        .execute[Either[ErrorResponse, Boolean]]
    }(ec, logger)
}
