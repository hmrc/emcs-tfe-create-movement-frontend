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

package services

import connectors.emcsTfe.MovementTemplatesConnector
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovementTemplatesService @Inject()(connector: MovementTemplatesConnector)
                                        (implicit ec: ExecutionContext) extends Logging {

  def userHasTemplates(ern: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    connector.getList(ern).map {
      case Left(_) =>
        logger.warn("[userHasTemplates] Failed to retrieve templates from emcs-tfe, defaulting response to false")
        false
      case Right(templates) =>
        templates.nonEmpty
    }
}
