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

package mocks.services

import models.requests.DataRequest
import models.response.{ErrorResponse, SubmitCreateMovementResponse}
import models.submitCreateMovement.SubmitCreateMovementModel
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import services.SubmitCreateMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockSubmitCreateMovementService extends MockFactory {

  lazy val mockSubmitCreateMovementService: SubmitCreateMovementService = mock[SubmitCreateMovementService]

  object MockSubmitCreateMovementService {

    def submit(model: SubmitCreateMovementModel, ern: String): CallHandler4[SubmitCreateMovementModel, String, DataRequest[_], HeaderCarrier, Future[Either[ErrorResponse, SubmitCreateMovementResponse]]] =
      (mockSubmitCreateMovementService.submit(_: SubmitCreateMovementModel, _: String)(_: DataRequest[_], _: HeaderCarrier))
        .expects(model, ern, *, *)
  }
}
