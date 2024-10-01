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
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovementTemplatesConnector @Inject()(val http: HttpClient,
                                           config: AppConfig) extends GetListOfTemplatesHttpParser with SaveTemplateHttpParser {

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def getList(ern: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, MovementTemplates]] =
    withExceptionRecovery("getList") {
      http.GET[Either[ErrorResponse, MovementTemplates]](s"$baseUrl/templates/$ern")
    }(ec, logger)

  def saveTemplate(movementTemplate: MovementTemplate)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    withExceptionRecovery("saveTemplate") {
      http.PUT[MovementTemplate, Either[ErrorResponse, Boolean]](
        s"$baseUrl/template/${movementTemplate.ern}/${movementTemplate.templateId}",
        movementTemplate
      )
    }(ec, logger)
}
