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

import base.SpecBase
import mocks.connectors.MockGetCnCodeInformationConnector
import models.UnitOfMeasure.Kilograms
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import models.response.{ReferenceDataException, UnexpectedDownstreamResponseError}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetCnCodeInformationServiceSpec extends SpecBase with MockGetCnCodeInformationConnector {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetCnCodeInformationService(mockGetCnCodeInformationConnector)

  val items = Seq(CnCodeInformationItem("T400", "24029000"))
  val request = CnCodeInformationRequest(items)

  ".getCnCodeInformationWithMovementItems" - {

    "should return Success response" - {

      "when Connector returns success from downstream" in {
        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Right(CnCodeInformationResponse(data = Map(
          "24029000" -> CnCodeInformation(
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          )
        )))))

        testService.getCnCodeInformation(items)(hc).futureValue mustBe Seq((items.head, CnCodeInformation(
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasure = Kilograms
        )))
      }
    }

    "should return Failure response" - {

      "when Connector returns failure from downstream" in {

        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[ReferenceDataException](await(testService.getCnCodeInformation(items)(hc)))

        result.getMessage must include(s"Failed to retrieve CN Code information")
      }

      "when not all items match something from the Connector" in {

        val items = Seq(
          CnCodeInformationItem("T400", "24029000"),
          CnCodeInformationItem("T401", "24029001"),
          CnCodeInformationItem("T402", "24029002")
        )
        val request = CnCodeInformationRequest(items)

        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Right(CnCodeInformationResponse(data = Map(
          "24029000" -> CnCodeInformation(
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          ),
          "24029001" -> CnCodeInformation(
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          )
        )))))

        val result = intercept[ReferenceDataException](await(testService.getCnCodeInformation(items)(hc)))

        result.getMessage must include(s"Failed to match item with CN Code information")
      }
    }
  }
}
