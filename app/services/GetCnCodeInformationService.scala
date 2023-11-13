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

import connectors.referenceData.GetCnCodeInformationConnector
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.ReferenceDataException
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetCnCodeInformationService @Inject()(connector: GetCnCodeInformationConnector)
                                           (implicit ec: ExecutionContext) {

  def getCnCodeInformation(items: Seq[CnCodeInformationItem])(implicit hc: HeaderCarrier): Future[Seq[(CnCodeInformationItem, CnCodeInformation)]] =
    connector.getCnCodeInformation(CnCodeInformationRequest(items)).map {
      case Right(response) =>
        matchMovementItemsWithReferenceDataValues(response, items).map {
          case (item, Some(information)) => (item, information)
          case (item, _) => throw ReferenceDataException(s"Failed to match item with CN Code information: $item")
        }
      case Left(errorResponse) => throw ReferenceDataException(s"Failed to retrieve CN Code information: $errorResponse")
    }

  private def matchMovementItemsWithReferenceDataValues(response: CnCodeInformationResponse,
                                                        items: Seq[CnCodeInformationItem]): Seq[(CnCodeInformationItem, Option[CnCodeInformation])] =
    items.map {
      case item if response.data.contains(item.cnCode) =>
        item -> Some(response.data(item.cnCode))
      case item =>
        item -> None
    }
}
