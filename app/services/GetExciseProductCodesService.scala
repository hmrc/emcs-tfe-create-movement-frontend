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

import connectors.referenceData.GetExciseProductCodesConnector
import models.requests.DataRequest
import models.response.ExciseProductCodesException
import models.{ExciseProductCode, NorthernIrelandTemporaryCertifiedConsignor}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetExciseProductCodesService @Inject()(connector: GetExciseProductCodesConnector)
                                            (implicit ec: ExecutionContext) {


  private[services] def removeS600IfDutySuspendedMovement(exciseProductCodes: Seq[ExciseProductCode])
                                                         (implicit request: DataRequest[_]): Seq[ExciseProductCode] = {
    //Only include S600 in list if consignor ERN = XIPC/ XIPTA (user is/should be asked for an XIPTA as part of a XIPC flow)
    request.userTypeFromErn match {
      case NorthernIrelandTemporaryCertifiedConsignor => exciseProductCodes
      case _ => exciseProductCodes.filterNot(_.code == "S600")
    }
  }

  def getExciseProductCodes()(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Future[Seq[ExciseProductCode]] = {
    connector.getExciseProductCodes().map {
      case Left(_) => throw ExciseProductCodesException("No excise product codes retrieved")
      case Right(exciseProductCodes) => removeS600IfDutySuspendedMovement(exciseProductCodes)
    }
  }
}
