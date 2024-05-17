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

import connectors.emcsTfe.DeleteDraftMovementConnector
import models.requests.DataRequest
import models.response.DeleteDraftMovementException
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteDraftMovementService @Inject()(deleteDraftMovementConnector: DeleteDraftMovementConnector)
                                          (implicit ec: ExecutionContext) {

  def deleteDraft()(implicit request: DataRequest[_], hc: HeaderCarrier): Future[Boolean] =
    deleteDraftMovementConnector.deleteDraft().map {
      case Left(_) => throw DeleteDraftMovementException("Failed to delete the users draft movement")
      case Right(_) => true
    }
}
