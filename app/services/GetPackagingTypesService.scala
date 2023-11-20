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

import connectors.referenceData.GetBulkPackagingTypesConnector
import models.response.PackagingTypesException
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPackagingTypesService @Inject()(connector: GetBulkPackagingTypesConnector)
                                        (implicit ec: ExecutionContext) {

  def getBulkPackagingTypes(packagingCodes: Seq[ItemBulkPackagingCode])(implicit hc: HeaderCarrier): Future[Seq[BulkPackagingType]] = {
    connector.getBulkPackagingTypes(packagingCodes).map {
      case Left(_) => throw PackagingTypesException("Invalid response from packaging types code endpoint")
      case Right(packagingTypes) => packagingTypes
    }
  }

}
