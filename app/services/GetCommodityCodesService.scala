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

import connectors.referenceData.GetCommodityCodesConnector
import models.ExciseProductCode
import models.response.CommodityCodesException
import models.response.referenceData.CnCodeInformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetCommodityCodesService @Inject()(connector: GetCommodityCodesConnector)
                                        (implicit ec: ExecutionContext) {

  def getCommodityCodes(exciseProductCode: ExciseProductCode)(implicit hc: HeaderCarrier): Future[Seq[CnCodeInformation]] = {
    connector.getCommodityCodes(exciseProductCode.code).map {
      case Left(_) => throw CommodityCodesException("Invalid response from commodity code endpoint")
      case Right(commodityCodes) => commodityCodes
    }
  }
}
