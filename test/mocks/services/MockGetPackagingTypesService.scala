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

import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import services.GetPackagingTypesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetPackagingTypesService extends MockFactory {

  lazy val mockGetPackagingTypesService: GetPackagingTypesService = mock[GetPackagingTypesService]

  object MockGetPackagingTypesService {

    def getBulkPackagingTypes(): CallHandler2[Seq[ItemBulkPackagingCode], HeaderCarrier, Future[Seq[BulkPackagingType]]] = {
      (mockGetPackagingTypesService.getBulkPackagingTypes(_: Seq[ItemBulkPackagingCode])(_: HeaderCarrier)).expects(*, *)
    }
  }
}
