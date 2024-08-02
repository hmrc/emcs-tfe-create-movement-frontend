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

package services

import config.AppConfig
import connectors.emcsTfe.SubmitCreateMovementConnector
import featureswitch.core.config.{EnableNRS, FeatureSwitching}
import models.audit.SubmitCreateMovementAudit
import models.requests.DataRequest
import models.response.{ErrorResponse, SubmitCreateMovementResponse}
import models.submitCreateMovement.SubmitCreateMovementModel
import services.nrs.NRSBrokerService
import uk.gov.hmrc.http.HeaderCarrier
import utils.{Logging, TimeMachine}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitCreateMovementService @Inject()(
                                             connector: SubmitCreateMovementConnector,
                                             nrsBrokerService: NRSBrokerService,
                                             auditingService: AuditingService,
                                             timeMachine: TimeMachine,
                                             override val config: AppConfig
                                           )(implicit ec: ExecutionContext) extends Logging with FeatureSwitching {

  def submit(submitCreateMovementModel: SubmitCreateMovementModel, ern: String)
            (implicit request: DataRequest[_], hc: HeaderCarrier): Future[Either[ErrorResponse, SubmitCreateMovementResponse]] = {
    if(isEnabled(EnableNRS)) {
      nrsBrokerService.submitPayload(submitCreateMovementModel, ern).flatMap { _ =>
        handleSubmission(submitCreateMovementModel)
      }
    } else {
      handleSubmission(submitCreateMovementModel)
    }
  }

  private def handleSubmission(submitCreateMovementModel: SubmitCreateMovementModel)
                              (implicit hc: HeaderCarrier, request: DataRequest[_]): Future[Either[ErrorResponse, SubmitCreateMovementResponse]] =
    connector.submit(submitCreateMovementModel).map { response =>
      writeAudit(submitCreateMovementModel, response)
      response
    }

  private def writeAudit(
                          submissionRequest: SubmitCreateMovementModel,
                          submissionResponse: Either[ErrorResponse, SubmitCreateMovementResponse]
                        )(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Unit =
    auditingService.audit(
      SubmitCreateMovementAudit(
        ern = dataRequest.ern,
        submissionRequest = submissionRequest,
        submissionResponse = submissionResponse,
        receiptDate = timeMachine.now().toString
      )
    )
}
