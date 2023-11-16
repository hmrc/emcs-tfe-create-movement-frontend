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

package connectors.referenceData
import models.UnitOfMeasure.Kilograms
import models.requests.CnCodeInformationRequest
import models.response.ErrorResponse
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class FakeGetCnCodeInformationConnector extends GetCnCodeInformationConnector {
  val fakeResponse = CnCodeInformationResponse(data = Map(
    "24029000" -> CnCodeInformation(
      cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
      exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
      unitOfMeasure = Kilograms
    )
  ))

  override def getCnCodeInformation(request: CnCodeInformationRequest)
                                   (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, CnCodeInformationResponse]] =
    Future.successful(Right(fakeResponse))
}
