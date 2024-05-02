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
import fixtures.ItemFixtures
import mocks.connectors.MockGetExciseProductCodesConnector
import models.requests.DataRequest
import models.response.{ExciseProductCodesException, UnexpectedDownstreamResponseError}
import models.sections.info.movementScenario.MovementScenario
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetExciseProductCodesServiceSpec extends SpecBase with MockGetExciseProductCodesConnector with ItemFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetExciseProductCodesService(mockGetExciseProductCodesConnector)

  ".getExciseProductCodes" - {

    "should return Seq[ExciseProductCode]" - {

      "when Connector returns success from downstream and guarantor is required" in {

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, true))

        val expectedResult = Seq(
          beerExciseProductCode,
          wineExciseProductCode,
          wineExciseProductCode300,
          spiritExciseProductCode,
          energyExciseProductCode
        )

        MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Right(Seq(beerExciseProductCode, wineExciseProductCode, wineExciseProductCode300, spiritExciseProductCode, s600ExciseProductCode, energyExciseProductCode))))

        val actualResults = testService.getExciseProductCodes().futureValue

        actualResults mustBe expectedResult
      }

      Seq(
        testNorthernIrelandErn -> MovementScenario.UkTaxWarehouse.NI,
        testGreatBritainErn -> MovementScenario.UkTaxWarehouse.GB
      ).foreach { case (userErn, scenario) =>
        s"when Connector returns success from downstream, trader is an ${userErn.take(2)} trader, guarantor is not required and destinationType is $scenario" in {

          implicit val request: DataRequest[_] = dataRequest(
            FakeRequest(),
            ern = testNorthernIrelandErn,
            answers = emptyUserAnswers
              .set(GuarantorRequiredPage, false)
              .set(DestinationTypePage, scenario)
          )

          val expectedResult = Seq(
            beerExciseProductCode,
            wineExciseProductCode,
            wineExciseProductCode300
          )

          MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Right(Seq(beerExciseProductCode, wineExciseProductCode, wineExciseProductCode300, spiritExciseProductCode, s600ExciseProductCode, energyExciseProductCode))))

          val actualResults = testService.getExciseProductCodes().futureValue

          actualResults mustBe expectedResult
        }
      }

      Seq(
        MovementScenario.EuTaxWarehouse,
        MovementScenario.TemporaryRegisteredConsignee,
        MovementScenario.RegisteredConsignee,
        MovementScenario.DirectDelivery,
        MovementScenario.ExemptedOrganisation
      ).foreach { scenario =>
        s"when Connector returns success from downstream, trader is an XI trader, guarantor is not required and destinaionType is $scenario" in {

          implicit val request: DataRequest[_] = dataRequest(
            FakeRequest(),
            ern = testNorthernIrelandErn,
            answers = emptyUserAnswers
              .set(GuarantorRequiredPage, false)
              .set(DestinationTypePage, scenario)
          )

          val expectedResult = Seq(
            energyExciseProductCode
          )

          MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Right(Seq(beerExciseProductCode, wineExciseProductCode, wineExciseProductCode300, spiritExciseProductCode, s600ExciseProductCode, energyExciseProductCode))))

          val actualResults = testService.getExciseProductCodes().futureValue

          actualResults mustBe expectedResult
        }
      }

      s"when Connector returns success from downstream, trader is an XI trader, and destinationType is ${MovementScenario.UnknownDestination}" in {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          ern = testNorthernIrelandErn,
          answers = emptyUserAnswers
            .set(DestinationTypePage, MovementScenario.UnknownDestination)
        )

        val expectedResult = Seq(
          energyExciseProductCode
        )

        MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Right(Seq(beerExciseProductCode, wineExciseProductCode, wineExciseProductCode300, spiritExciseProductCode, s600ExciseProductCode, energyExciseProductCode))))

        val actualResults = testService.getExciseProductCodes().futureValue

        actualResults mustBe expectedResult

      }

      "when Connector returns success from downstream, trader is XIPA - include S600 in results" in {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          ern = testNIDutyPaidErn
        )

        val expectedResult = Seq(beerExciseProductCode, wineExciseProductCode, wineExciseProductCode300, spiritExciseProductCode, s600ExciseProductCode, energyExciseProductCode)

        MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Right(expectedResult)))

        val actualResults = testService.getExciseProductCodes().futureValue

        actualResults mustBe expectedResult

      }
    }
  }

  "should throw ExciseProductCodesException" - {

    "when Connector returns failure from downstream" in {

      implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, true))
      val expectedResult = "No excise product codes retrieved"

      MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Left(UnexpectedDownstreamResponseError)))

      val actualResult = intercept[ExciseProductCodesException](await(testService.getExciseProductCodes())).getMessage

      actualResult mustBe expectedResult
    }
  }
}
