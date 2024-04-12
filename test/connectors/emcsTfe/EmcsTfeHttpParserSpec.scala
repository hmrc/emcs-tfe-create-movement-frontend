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

package connectors.emcsTfe

import base.SpecBase
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, SubmitCreateMovementResponse, UnexpectedDownstreamDraftSubmissionResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.{ExecutionContext, Future}

class EmcsTfeHttpParserSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with LogCapturing {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val httpParser = new EmcsTfeHttpParser[SubmitCreateMovementResponse] {
    override implicit val reads: Reads[SubmitCreateMovementResponse] = SubmitCreateMovementResponse.reads
    override def http: HttpClient = mockHttpClient
  }

  "EmcsTfeReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid JSON is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.OK, successResponseEISJson, Map())

        httpParser.EmcsTfeReads.read("POST", "/create-movement/ern/draftId", httpResponse) mustBe Right(submitCreateMovementResponseEIS)
      }
    }

    "should return UnexpectedDownstreamDraftSubmissionResponseError" - {

      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.EmcsTfeReads.read("POST", "/create-movement/ern/draftId", httpResponse) mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(Status.INTERNAL_SERVER_ERROR))
      }
    }

    "should return JsonValidationError" - {

      s"when response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.EmcsTfeReads.read("POST", "/create-movement/ern/draftId", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.obj(), Map())

        httpParser.EmcsTfeReads.read("POST", "/create-movement/ern/draftId", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }

  "post" - {

    "should return the result of the call (if no exception thrown)" in {

      val response = HttpResponse(Status.OK, successResponseEISJson, Map())

      MockHttpClient.post("/create-movement/ern/draftId", submitCreateMovementResponseEIS)
        .returns(Future.successful(Right(response)))

      httpParser.post("/create-movement/ern/draftId", submitCreateMovementResponseEIS).futureValue mustBe Right(response)
    }

    "should return UnexpectedDownstreamDraftSubmissionResponseError(ISE) when an exception is thrown" in {

      MockHttpClient.post("/create-movement/ern/draftId", submitCreateMovementResponseEIS)
        .returns(Future.failed(new Exception("Canned")))

      httpParser.post("/create-movement/ern/draftId", submitCreateMovementResponseEIS).futureValue mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))
    }
  }

  "withExceptionRecovery" - {

    "should return UnexpectedDownstreamDraftSubmissionResponseError" - {

      "when the provided function throws an exception" in {

        withCaptureOfLoggingFrom(httpParser.logger) { logs =>

          httpParser.withExceptionRecovery("test")(Future.failed(new Exception("Canned"))).futureValue mustBe Left(UnexpectedDownstreamDraftSubmissionResponseError(INTERNAL_SERVER_ERROR))

          logs.exists(_.getMessage.contains("[test] Unexpected exception of type Exception was thrown")) mustBe true
        }
      }
    }
  }
}
