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
import fixtures.TemplateFixtures
import mocks.connectors.MockHttpClient
import models.response.templates.MovementTemplates
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.ExecutionContext

class GetListOfTemplatesHttpParserSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with LogCapturing
  with GetListOfTemplatesHttpParser
  with TemplateFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "GetListOfTemplatesReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid JSON is returned that can be parsed to the model" in {
        val httpResponse = HttpResponse(
          status = Status.OK,
          json = Json.obj("templates" -> Json.arr(templateJson), "count" -> 1),
          headers = Map()
        )
        GetListOfTemplatesReads.read("", "", httpResponse) mustBe Right(MovementTemplates(Seq(templateModel), 1))
      }

      "when valid NO_CONTENT is returned" in {
        val httpResponse = HttpResponse(Status.NO_CONTENT, Json.obj(), Map())
        GetListOfTemplatesReads.read("", "", httpResponse) mustBe Right(MovementTemplates.empty)
      }
    }

    "should return UnexpectedDownstreamDraftSubmissionResponseError" - {

      s"when status is not OK (${Status.OK})" in {
        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())
        GetListOfTemplatesReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return JsonValidationError" - {

      s"when response does not contain Json" in {
        val httpResponse = HttpResponse(Status.OK, "", Map())
        GetListOfTemplatesReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {
        val httpResponse = HttpResponse(Status.OK, Json.obj(), Map())
        GetListOfTemplatesReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }
}
