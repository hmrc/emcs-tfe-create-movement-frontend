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

import connectors.emcsTfe.SubmitCreateMovementConnector
import models.requests.DataRequest
import models.response.{SubmitCreateMovementException, SubmitCreateMovementResponse}
import models.submitCreateMovement.SubmitCreateMovementModel
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitCreateMovementService @Inject()(connector: SubmitCreateMovementConnector)(implicit ec: ExecutionContext) extends Logging {
  def submit(submitCreateMovementModel: SubmitCreateMovementModel)
            (implicit request: DataRequest[_], hc: HeaderCarrier): Future[SubmitCreateMovementResponse] = {

    // audit request

    connector.submit(submitCreateMovementModel).map {
      case Right(success) =>
        // audit response

        success
      case Left(value) =>
        logger.warn(s"Received Left from SubmitCreateMovementConnector: $value")
        throw SubmitCreateMovementException(s"Failed to submit Create Movement to emcs-tfe for ern: '${request.ern}' & draftId: '${request.draftId}'")
    }

  }
}
