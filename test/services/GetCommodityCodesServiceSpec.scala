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

import base.SpecBase
import fixtures.ItemFixtures
import mocks.connectors.MockGetCommodityCodesConnector
import models.response.{CommodityCodesException, JsonValidationError}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetCommodityCodesServiceSpec extends SpecBase with MockGetCommodityCodesConnector with ItemFixtures {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val service = new GetCommodityCodesService(mockGetCommodityCodesConnector)

  ".getCommodityCodes" - {

    "should throw a CommodityCodesException" - {

      "when the connector returns a Left" in {

        MockGetCommodityCodesConnector.getCommodityCodes(testCnCodeWine).returns(Future.successful(Left(JsonValidationError)))

        intercept[CommodityCodesException](await(service.getCommodityCodes(testCnCodeWine))).message mustBe "Invalid response from commodity code endpoint"
      }
    }

    "should return the commodity codes" - {

      "when the connector returns a Right" in {

        MockGetCommodityCodesConnector.getCommodityCodes(testCnCodeWine).returns(Future.successful(Right(Seq(testCommodityCodeTobacco))))

        service.getCommodityCodes(testCnCodeWine).futureValue mustBe Seq(testCommodityCodeTobacco)
      }
    }
  }

}
