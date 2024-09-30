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
import models.requests.DataRequest
import models.response.templates.MovementTemplates
import models.response.{ErrorResponse, TemplatesException}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{Logging, TimeMachine, UUIDGenerator}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovementTemplatesService @Inject()(connector: MovementTemplatesConnector)
                                        (implicit ec: ExecutionContext,
                                         uuid: UUIDGenerator,
                                         timeMachine: TimeMachine) extends Logging {

  def getList(ern: String)(implicit hc: HeaderCarrier): Future[MovementTemplates] =
    connector.getList(ern).map {
      case Right(templates) => templates
      case Left(_) =>
        logger.warn(s"[getList] Failed to retrieve templates from emcs-tfe for ern: $ern")
        throw TemplatesException(s"Failed to retrieve templates from emcs-tfe for ern: $ern")
    }

  def userHasTemplates(ern: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    getList(ern).map(_.count > 0).recoverWith { _ =>
      logger.warn("[userHasTemplates] Failed to retrieve templates from emcs-tfe, defaulting response to false")
      Future.successful(false)
    }

  def getExistingTemplateNames(ern: String)(implicit hc: HeaderCarrier): Future[Seq[String]] =
    getList(ern).map(_.templates.map(_.templateName))

  def saveTemplate(templateName: String, existingIdToUpdate: Option[String] = None)
                  (implicit request: DataRequest[_], hc: HeaderCarrier): Future[Either[ErrorResponse, Boolean]] =
    connector.saveTemplate(request.userAnswers.toTemplate(templateName, existingIdToUpdate))
}
