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

import base.SpecBase
import mocks.connectors.MockHttpClient
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.BeforeAndAfterAll
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMemberStatesConnectorSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with BeforeAndAfterAll {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new GetMemberStatesConnectorImpl(mockHttpClient, appConfig)

  "getMemberStates" - {

    "should return a successful response" - {

      "when downstream call is successful" in {

        val expectedResult = Right(Seq(memberStateJsonAT, memberStateJsonBE))

        MockHttpClient.get(
          url = s"${appConfig.referenceDataBaseUrl}/oracle/member-states"
        ).returns(Future.successful(expectedResult))

        val actualResult = connector.getMemberStates().futureValue

        actualResult mustBe expectedResult
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        val expectedResult = Left(UnexpectedDownstreamResponseError)

        MockHttpClient.get(
          url = s"${appConfig.referenceDataBaseUrl}/oracle/member-states"
        ).returns(Future.successful(expectedResult))

        val actualResult = connector.getMemberStates().futureValue

        actualResult mustBe expectedResult
      }
    }
  }
}

